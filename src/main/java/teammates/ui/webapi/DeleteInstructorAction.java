package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Deletes an instructor from a course, unless it's the last instructor in the course.
 */
public class DeleteInstructorAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        gateKeeper.verifyCanModifyInstructor(requestContext, userId);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        logic.deleteInstructorCascade(userId);

        return new JsonResult("Instructor is successfully deleted.");
    }
}
