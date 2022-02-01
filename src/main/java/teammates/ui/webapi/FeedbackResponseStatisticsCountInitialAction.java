package teammates.ui.webapi;

import teammates.common.util.Logger;

/**
 * Task queue worker action: prepares session unpublished reminder for a particular session to be sent.
 */
public class FeedbackResponseStatisticsCountInitialAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
/*         String feedbackSessionName = getNonNullRequestParamValue(ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);

        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        if (session == null) {
            log.severe("Feedback session object for feedback session name: " + feedbackSessionName
                       + " for course: " + courseId + " could not be fetched.");
            return new JsonResult("Failure");
        }
        List<EmailWrapper> emailsToBeSent = emailGenerator.generateFeedbackSessionUnpublishedEmails(session);
        try {
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);
            logic.updateFeedbackSession(
                    FeedbackSessionAttributes
                            .updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                            .withSentPublishedEmail(false)
                            .build());
        } catch (Exception e) {
            log.severe("Unexpected error", e);
        }
 */     return new JsonResult("Successful");
    }
}
