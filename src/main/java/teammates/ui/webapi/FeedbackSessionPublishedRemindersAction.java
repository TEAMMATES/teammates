package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Cron job: schedules feedback session published emails to be sent.
 */
public class FeedbackSessionPublishedRemindersAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        List<FeedbackSession> sessions = sqlLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        for (FeedbackSession session : sessions) {
            RequestTracer.checkRemainingTime();
            java.util.List<teammates.common.util.EmailWrapper> emailsToBeSent =
                    sqlEmailGenerator.generateFeedbackSessionPublishedEmails(session);
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);
            session.setPublishedEmailSent(true);
            sqlLogic.adjustFeedbackSessionEmailStatusAfterUpdate(session);
        }

        return new JsonResult("Successful");
    }

}
