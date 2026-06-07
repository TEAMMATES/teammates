package teammates.ui.webapi;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseViewData;
import teammates.ui.output.CoursesData;
import teammates.ui.output.InstructorCoursePermissionsData;

/**
 * Gets all courses for the instructor, and filtered by active and soft-deleted.
 * Or gets all courses for the student he belongs to.
 */
public class GetCoursesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // No additional access control needed as both students and instructors can access this action
        // as the courses returned are filtered based on the user.
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
        List<Course> courses = logic.getCoursesForStudentAccount(requestContext.getAccount());
        CoursesData coursesData = new CoursesData(courses);
        List<CourseViewData> courseDataList = coursesData.getCourses();

        courseDataList.forEach(courseData -> courseData.getCourse().hideInformationForStudent());

        return new JsonResult(coursesData);
    }

    private JsonResult getInstructorCourses() {
        String courseStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_STATUS);

        List<Instructor> instructors = logic.getInstructorsByAccountId(requestContext.getAccount().getId());
        List<Course> courses;

        switch (courseStatus) {
        case Const.CourseStatus.ACTIVE:
            courses = logic.getCoursesForInstructors(instructors);
            break;
        case Const.CourseStatus.SOFT_DELETED:
            courses = logic.getSoftDeletedCoursesForInstructors(instructors);
            break;
        default:
            throw new InvalidHttpParameterException("Error: invalid course status");
        }

        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        CoursesLogic.sortById(courses);
        CoursesData coursesData = new CoursesData(courses);
        List<CourseViewData> coursesDataList = coursesData.getCourses();

        coursesDataList.sort(Comparator.comparing(courseData -> courseData.getCourse().getCourseId()));
        coursesDataList.forEach(courseData -> {
            Instructor instructor = courseIdToInstructor.get(courseData.getCourse().getCourseId());
            if (instructor == null) {
                return;
            }
            courseData.setInstructorPermissions(new InstructorCoursePermissionsData(
                    logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_COURSE),
                    logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_STUDENT),
                    logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)));
        });

        return new JsonResult(coursesData);
    }
}
