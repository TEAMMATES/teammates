package teammates.ui.webapi;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.sqllogic.core.CoursesLogic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;
import teammates.ui.output.CoursesData;

/**
 * Gets all courses for the instructor, and filtered by active, archived and soft-deleted.
 * Or gets all courses for the student he belongs to.
 */
public class GetCoursesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(entityType.equals(Const.EntityType.STUDENT) && userInfo.isStudent)
                && !(entityType.equals(Const.EntityType.INSTRUCTOR) && userInfo.isInstructor)) {
            throw new UnauthorizedAccessException("Current account cannot access to courses of request entity type");
        }
    }

    @Override
    public JsonResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        switch (entityType) {
        case Const.EntityType.STUDENT:
            return getStudentCourses();
        case Const.EntityType.INSTRUCTOR:
            return getInstructorCourses();
        default:
            throw new InvalidHttpParameterException("Error: invalid entity type");
        }
    }

    private JsonResult getStudentCourses() {
        List<Course> sqlCourses = sqlLogic.getCoursesForStudentAccount(userInfo.id);

        List<CourseAttributes> courses = logic
                .getCoursesForStudentAccount(userInfo.id)
                .stream()
                .filter(course -> !isCourseMigrated(course.getId()))
                .collect(Collectors.toList());

        CoursesData coursesData = new CoursesData(sqlCourses);

        List<CourseData> courseData = coursesData.getCourses();

        List<CourseData> datastoreCourseData =
                courses.stream().map(CourseData::new).collect(Collectors.toList());

        courseData.addAll(datastoreCourseData);
        courseData.forEach(CourseData::hideInformationForStudent);
        return new JsonResult(coursesData);
    }

    private JsonResult getInstructorCourses() {
        String courseStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_STATUS);

        List<InstructorAttributes> instructors;
        List<CourseAttributes> courses;

        List<Instructor> sqlInstructors;
        List<Course> sqlCourses;

        switch (courseStatus) {
        case Const.CourseStatus.ACTIVE:
            instructors = logic.getInstructorsForGoogleId(userInfo.id, true);
            courses = getCourse(instructors);

            sqlInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.id);
            sqlInstructors =
                    sqlInstructors
                            .stream()
                            .filter(instructor -> !instructor.getIsArchived())
                            .collect(Collectors.toList());
            sqlCourses = sqlLogic.getCoursesForInstructors(sqlInstructors);

            break;
        case Const.CourseStatus.ARCHIVED:
            instructors = logic.getInstructorsForGoogleId(userInfo.id)
                    .stream()
                    .filter(InstructorAttributes::isArchived)
                    .collect(Collectors.toList());
            courses = getCourse(instructors);

            sqlInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.id)
                    .stream()
                    .filter(Instructor::getIsArchived)
                    .collect(Collectors.toList());
            sqlCourses = sqlLogic.getCoursesForInstructors(sqlInstructors);

            break;
        case Const.CourseStatus.SOFT_DELETED:
            instructors = logic.getInstructorsForGoogleId(userInfo.id);
            courses = getSoftDeletedCourse(instructors);

            sqlInstructors = sqlLogic.getInstructorsForGoogleId(userInfo.id);
            sqlCourses = sqlLogic.getSoftDeletedCoursesForInstructors(sqlInstructors);

            break;
        default:
            throw new InvalidHttpParameterException("Error: invalid course status");
        }

        courses = courses.stream()
                .filter(course -> !isCourseMigrated(course.getId()))
                .collect(Collectors.toList());

        Map<String, InstructorAttributes> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        Map<String, Instructor> sqlCourseIdToInstructor = new HashMap<>();
        sqlInstructors.forEach(instructor -> sqlCourseIdToInstructor.put(instructor.getCourseId(), instructor));

        CourseAttributes.sortById(courses);

        CoursesLogic.sortById(sqlCourses);

        CoursesData coursesData = new CoursesData(sqlCourses);

        List<CourseData> courseData = coursesData.getCourses();

        List<CourseData> datastoreCourseData =
                courses.stream().map(CourseData::new).collect(Collectors.toList());

        courseData.addAll(datastoreCourseData);

        // TODO: Remove once migration is completed
        courseData.sort(Comparator.comparing(CourseData::getCourseId));

        courseData.forEach(cData -> {
            InstructorAttributes instructor = courseIdToInstructor.get(cData.getCourseId());
            if (instructor == null) {
                return;
            }
            InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, null);
            cData.setPrivileges(privilege);
        });

        return new JsonResult(coursesData);
    }

    private List<CourseAttributes> getCourse(List<InstructorAttributes> instructors) {
        return logic.getCoursesForInstructor(instructors);
    }

    private List<CourseAttributes> getSoftDeletedCourse(List<InstructorAttributes> instructors) {
        return logic.getSoftDeletedCoursesForInstructors(instructors);
    }
}
