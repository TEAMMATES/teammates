package teammates.ui.webapi;

import java.util.List;

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
