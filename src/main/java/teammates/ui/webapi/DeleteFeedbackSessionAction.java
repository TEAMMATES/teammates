package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;

/**
 * Delete a feedback session.
 */
class DeleteFeedbackSessionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        logic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);

        return new JsonResult("The feedback session is deleted.");
    }

}
