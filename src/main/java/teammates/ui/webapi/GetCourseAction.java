package teammates.ui.webapi;

import teammates.common.datatransfer.SessionKeyType;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.output.CourseViewData;
import teammates.ui.output.InstructorCoursePermissionsData;

/**
 * Get a course for an instructor or student.
 */
public class GetCourseAction extends RegKeyAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        gateKeeper.verifySessionKey(requestContext, SessionKeyType.SUBMISSION, SessionKeyType.RESULTS);

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            gateKeeper.verifyInstructorInCourse(requestContext, courseId);
            return;
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            gateKeeper.verifyStudentInCourse(requestContext, courseId);
            return;
        }

        throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Course course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }

        CourseViewData output = new CourseViewData(new CourseData(course));
        String entityType = getRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            Instructor instructor = getInstructorFromRequest(courseId);
            if (instructor != null) {
                output.setInstructorPermissions(new InstructorCoursePermissionsData(
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_COURSE),
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_STUDENT),
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)));
            }
        }

        return new JsonResult(output);
    }
}
