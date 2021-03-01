package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;

/**
 * Remind the student about the published result of a feedback session.
 */
class RemindFeedbackSessionResultAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        if (!feedbackSession.isPublished()) {
            return new JsonResult("Published email could not be resent "
                    + "as the feedback session is not published.", HttpStatus.SC_BAD_REQUEST);
        }

        FeedbackSessionRespondentRemindRequest remindRequest =
                getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
        String[] usersToEmail = remindRequest.getUsersToRemind();

        taskQueuer.scheduleFeedbackSessionResendPublishedEmail(courseId, feedbackSessionName, usersToEmail);

        return new JsonResult("Reminders sent");
    }
}
