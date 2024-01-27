package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Cron job: schedules feedback session closed emails to be sent.
 */
public class FeedbackSessionClosedRemindersAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        List<FeedbackSessionAttributes> sessionAttributes = logic.getFeedbackSessionsClosedWithinThePastHour();

        for (FeedbackSessionAttributes session : sessionAttributes) {
            // If course has been migrated, use sql email logic instead.
            if (isCourseMigrated(session.getCourseId())) {
                continue;
            }

            RequestTracer.checkRemainingTime();
            List<EmailWrapper> emailsToBeSent = emailGenerator.generateFeedbackSessionClosedEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                logic.updateFeedbackSession(
                        FeedbackSessionAttributes
                                .updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                                .withSentClosedEmail(true)
                                .build());
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }

        List<FeedbackSession> sessions = sqlLogic.getFeedbackSessionsClosedWithinThePastHour();

        for (FeedbackSession session : sessions) {
            RequestTracer.checkRemainingTime();
            List<EmailWrapper> emailsToBeSent = sqlEmailGenerator.generateFeedbackSessionClosedEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                session.setClosedEmailSent(true);
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }

        return new JsonResult("Successful");
    }

}
