package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorFeedbackPublishEmailParticularStudentsAction extends Action {

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

        if (!feedbackSession.isPublished()) {
            statusToUser.add(new StatusMessage(
                    Const.StatusMessages.FEEDBACK_SESSION_RESEND_LINKS_NOT_PUBLISHED, StatusMessageColor.DANGER));
            statusToAdmin = "Reminder email could not be sent out as the feedback session is not published.";
            return createRedirectResult(nextUrl);
        }

        String[] usersToEmail = getRequestParamValues(Const.ParamsNames.SEND_PUBLISHED_EMAIL);
        if (usersToEmail == null || usersToEmail.length == 0) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_RESEND_LINKS_EMPTY_RECIPIENT,
                                               StatusMessageColor.DANGER));
            return createRedirectResult(nextUrl);
        }

        taskQueuer.scheduleFeedbackSessionPublishedEmailParticularUsersEmail(courseId, feedbackSessionName, usersToEmail);

        statusToUser.add(new StatusMessage(
                Const.StatusMessages.FEEDBACK_SESSION_RESEND_LINKS_EMAIL_SENT, StatusMessageColor.SUCCESS));
        statusToAdmin = "Email sent out to the selected user(s): ";
        for (String user : usersToEmail) {
            statusToAdmin += "<br>" + user;
        }
        statusToAdmin += "<br>in Feedback Session <span class=\"bold\">(" + feedbackSessionName
                         + ")</span> " + "of Course <span class=\"bold\">[" + courseId + "]</span>";

        return createRedirectResult(nextUrl);
    }
}
