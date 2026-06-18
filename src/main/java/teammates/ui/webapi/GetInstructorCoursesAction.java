package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorCoursePermissionsData;
import teammates.ui.output.InstructorCoursesData;

/**
 * Gets all courses for the logged-in instructor, filtered by active or soft-deleted status.
 */
public class GetInstructorCoursesAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Courses are filtered to those where the user is an instructor.
    }

    @Override
    public JsonResult execute() {
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

        Map<String, InstructorCoursePermissionsData> permissionsByCourseId = new HashMap<>();
        for (Course course : courses) {
            Instructor instructor = courseIdToInstructor.get(course.getId());
            if (instructor == null) {
                continue;
            }
            permissionsByCourseId.put(course.getId(), new InstructorCoursePermissionsData(
                    logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_COURSE),
                    logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_STUDENT),
                    logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)));
        }

        return new JsonResult(new InstructorCoursesData(courses, permissionsByCourseId));
    }
}
