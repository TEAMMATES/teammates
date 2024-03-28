package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Task queue worker action: prepares session unpublished reminder for a particular session to be sent.
 */
public class FeedbackSessionUnpublishedEmailWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        String feedbackSessionName = getNonNullRequestParamValue(ParamsNames.FEEDBACK_SESSION_NAME);
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
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
            return new JsonResult("Successful");
        }

        FeedbackSession session = sqlLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (session == null) {
            log.severe("Feedback session object for feedback session name: " + feedbackSessionName
                    + " for course: " + courseId + " could not be fetched.");
            return new JsonResult("Failure");
        }

        List<EmailWrapper> emailsToBeSent = sqlEmailGenerator.generateFeedbackSessionUnpublishedEmails(session);
        try {
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);

            session.setPublishedEmailSent(false);
            sqlLogic.adjustFeedbackSessionEmailStatusAfterUpdate(session);
        } catch (Exception e) {
            log.severe("Unexpected error", e);
        }
        return new JsonResult("Successful");
    }

}
