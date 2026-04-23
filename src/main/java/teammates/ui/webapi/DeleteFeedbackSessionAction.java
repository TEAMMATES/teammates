package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;

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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = sqlLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()), feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        sqlLogic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);

        return new JsonResult("The feedback session is deleted.");
    }

}
