package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorFeedbackRemindAction extends Action {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);

        if (nextUrl == null) {
            nextUrl = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE;
        }

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                feedbackSession,
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        if (!feedbackSession.isOpened()) {
            statusToUser.add(new StatusMessage(
                    Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN, StatusMessageColor.DANGER));
            statusToAdmin = "Reminder email could not be sent out as the feedback session is not open for submissions.";
            return createRedirectResult(nextUrl);
        }

        taskQueuer.scheduleFeedbackSessionReminders(courseId, feedbackSessionName, account.googleId);

        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT, StatusMessageColor.SUCCESS));
        statusToAdmin = "Email sent out to all students who have not completed "
                      + "Feedback Session <span class=\"bold\">(" + feedbackSessionName
                      + ")</span> " + "of Course <span class=\"bold\">[" + courseId + "]</span>";

        return createRedirectResult(nextUrl);
    }

}
