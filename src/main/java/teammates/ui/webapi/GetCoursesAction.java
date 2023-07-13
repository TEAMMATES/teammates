package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;
import teammates.ui.output.CoursesData;

/**
 * Gets all courses for the instructor, and filtered by active, archived and soft-deleted.
 * Or gets all courses for the student he belongs to.
 */
class GetCoursesAction extends Action {

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
        List<CourseAttributes> courses = logic.getCoursesForStudentAccount(userInfo.id);
        CoursesData coursesData = new CoursesData(courses);
        coursesData.getCourses().forEach(CourseData::hideInformationForStudent);
        return new JsonResult(coursesData);
    }

    private JsonResult getInstructorCourses() {
        String courseStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_STATUS);
        List<CourseAttributes> courses;
        List<InstructorAttributes> instructors;
        switch (courseStatus) {
        case Const.CourseStatus.ACTIVE:
            instructors = logic.getInstructorsForGoogleId(userInfo.id, true);
            courses = getCourse(instructors);
            break;
        case Const.CourseStatus.ARCHIVED:
            instructors = logic.getInstructorsForGoogleId(userInfo.id)
                    .stream()
                    .filter(InstructorAttributes::isArchived)
                    .collect(Collectors.toList());
            courses = getCourse(instructors);
            break;
        case Const.CourseStatus.SOFT_DELETED:
            instructors = logic.getInstructorsForGoogleId(userInfo.id);
            courses = getSoftDeletedCourse(instructors);
            break;
        default:
            throw new InvalidHttpParameterException("Error: invalid course status");
        }

        Map<String, InstructorAttributes> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        CourseAttributes.sortById(courses);
        CoursesData coursesData = new CoursesData(courses);
        coursesData.getCourses().forEach(courseData -> {
            InstructorAttributes instructor = courseIdToInstructor.get(courseData.getCourseId());
            if (instructor == null) {
                return;
            }
            InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, null);
            courseData.setPrivileges(privilege);
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
