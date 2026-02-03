package teammates.ui.webapi;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.InstructorPermissionSet;
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

        if (!(Const.EntityType.STUDENT.equals(entityType) && userInfo.isStudent)
                && !(Const.EntityType.INSTRUCTOR.equals(entityType) && userInfo.isInstructor)) {
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
        List<Course> courses = sqlLogic.getCoursesForStudentAccount(userInfo.id);
        CoursesData coursesData = new CoursesData(courses);
        List<CourseData> courseDataList = coursesData.getCourses();

        courseDataList.forEach(CourseData::hideInformationForStudent);

        return new JsonResult(coursesData);
    }

    private JsonResult getInstructorCourses() {
        String courseStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_STATUS);

        List<Instructor> instructors = sqlLogic.getInstructorsForGoogleId(userInfo.id);
        List<Course> courses;

        switch (courseStatus) {
        case Const.CourseStatus.ACTIVE:
            courses = sqlLogic.getCoursesForInstructors(instructors);
            break;
        case Const.CourseStatus.SOFT_DELETED:
            courses = sqlLogic.getSoftDeletedCoursesForInstructors(instructors);
            break;
        default:
            throw new InvalidHttpParameterException("Error: invalid course status");
        }

        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        CoursesLogic.sortById(courses);
        CoursesData coursesData = new CoursesData(courses);
        List<CourseData> coursesDataList = coursesData.getCourses();

        coursesDataList.sort(Comparator.comparing(CourseData::getCourseId));
        coursesDataList.forEach(courseData -> {
            Instructor instructor = courseIdToInstructor.get(courseData.getCourseId());
            if (instructor == null) {
                return;
            }
            InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, null);
            courseData.setPrivileges(privilege);
        });

        return new JsonResult(coursesData);
    }
}
