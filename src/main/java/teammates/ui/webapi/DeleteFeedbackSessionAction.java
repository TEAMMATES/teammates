package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;

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

        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new UnauthorizedAccessException("The feedback session does not exist.");
        }
        Instructor instructor = sqlLogic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());
        gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        sqlLogic.deleteFeedbackSessionCascade(feedbackSessionId);

        return new JsonResult("The feedback session is deleted.");
    }

}
