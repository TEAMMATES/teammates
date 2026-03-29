package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Cron job: schedules feedback session opening soon emails to be sent.
 */
public class FeedbackSessionOpeningSoonRemindersAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        List<FeedbackSession> sessions = sqlLogic.getFeedbackSessionsOpeningWithinTimeLimit();
        for (FeedbackSession session : sessions) {
            RequestTracer.checkRemainingTime();
            List<EmailWrapper> emailsToBeSent = sqlEmailGenerator.generateFeedbackSessionOpeningSoonEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                session.setOpeningSoonEmailSent(true);
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }

        return new JsonResult("Successful");
    }
}
