package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.logic.entity.FeedbackSession;

/**
 * Cron job: schedules feedback session closed emails to be sent.
 */
public class FeedbackSessionClosedRemindersAction extends AutomatedServiceAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        List<FeedbackSession> sessions = logic.getFeedbackSessionsClosedWithinThePastHour();

        for (FeedbackSession session : sessions) {
            RequestTracer.checkRemainingTime();
            List<EmailWrapper> emailsToBeSent = emailGenerator.generateFeedbackSessionClosedEmails(session);
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
