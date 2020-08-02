package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.request.FeedbackSessionRespondentRemindRequest;

/**
 * Remind the student about the published result of a feedback session.
 */
public class RemindFeedbackSessionResultAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
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
