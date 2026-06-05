package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
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
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        Instructor instructorToDelete = logic.getInstructor(userId);
        if (instructorToDelete == null) {
            throw new EntityNotFoundException("Instructor with user ID " + userId + " does not exist.");
        }

        //allow access to admins or instructor with modify permission
        if (authContext.isAdmin()) {
            return;
        }

        Instructor instructor = getInstructorFromRequest(instructorToDelete.getCourseId());
        gateKeeper.verifyInstructorInCourse(authContext, instructorToDelete.getCourseId());
        gateKeeper.verifyInstructorHasPrivilege(instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        logic.deleteInstructorCascade(userId);

        return new JsonResult("Instructor is successfully deleted.");
    }
}
