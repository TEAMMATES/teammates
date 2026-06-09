package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Delete a feedback session.
 */
public class DeleteFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        gateKeeper.verifyInstructorHasPrivilegeInFeedbackSession(requestContext, feedbackSessionId,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        logic.deleteFeedbackSessionCascade(feedbackSessionId);

        return new JsonResult("The feedback session is deleted.");
    }

}
