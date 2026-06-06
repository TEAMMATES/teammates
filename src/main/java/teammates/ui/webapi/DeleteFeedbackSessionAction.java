package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
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

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new UnauthorizedAccessException("The feedback session does not exist.");
        }
        gateKeeper.verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(),
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        logic.deleteFeedbackSessionCascade(feedbackSessionId);

        return new JsonResult("The feedback session is deleted.");
    }

}
