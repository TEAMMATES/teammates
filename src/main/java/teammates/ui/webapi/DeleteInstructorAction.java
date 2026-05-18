package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Deletes an instructor from a course, unless it's the last instructor in the course.
 */
public class DeleteInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        //allow access to admins or instructor with modify permission
        if (authContext.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Instructor instructor = logic.getInstructorByGoogleId(courseId, authContext.id());
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor;
        if (instructorId != null) {
            instructor = logic.getInstructorByGoogleId(courseId, instructorId);
        } else if (instructorEmail != null) {
            instructor = logic.getInstructorForEmail(courseId, instructorEmail);
        } else {
            throw new InvalidHttpParameterException("Instructor to delete not specified");
        }

        if (instructor == null) {
            return new JsonResult("Instructor is successfully deleted.");
        }

        // Deleting last instructor from the course is not allowed (even by admins)
        if (!logic.hasAlternativeInstructor(courseId, instructor.getEmail())) {
            throw new InvalidOperationException(
                    "The instructor you are trying to delete is the last instructor in the course. "
                            + "Deleting the last instructor from the course is not allowed.");
        }

        logic.deleteInstructorCascade(courseId, instructor.getEmail());

        return new JsonResult("Instructor is successfully deleted.");
    }
}
