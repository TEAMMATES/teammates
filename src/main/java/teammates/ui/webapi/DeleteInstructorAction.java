package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
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

        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        Instructor instructorToDelete = logic.getInstructor(userId);
        if (instructorToDelete == null) {
            return;
        }

        Instructor instructor = logic.getInstructorByGoogleId(instructorToDelete.getCourseId(), getCurrentUserGoogleId());
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(instructorToDelete.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        logic.deleteInstructorCascade(userId);

        return new JsonResult("Instructor is successfully deleted.");
    }
}
