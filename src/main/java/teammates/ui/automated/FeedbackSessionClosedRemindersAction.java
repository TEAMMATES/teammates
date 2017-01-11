package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * Cron job: schedules feedback session closed emails to be sent.
 */
public class FeedbackSessionClosedRemindersAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return "send closed reminders";
    }
    
    @Override
    protected String getActionMessage() {
        return "Generating reminders for closed feedback sessions.";
    }
    
    @Override
    public void execute() {
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        List<FeedbackSessionAttributes> sessions = fsLogic.getFeedbackSessionsClosedWithinThePastHour();
        
        for (FeedbackSessionAttributes session : sessions) {
            List<EmailWrapper> emailsToBeSent = new EmailGenerator().generateFeedbackSessionClosedEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                session.setSentClosedEmail(true);
                fsLogic.updateFeedbackSession(session);
            } catch (Exception e) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
            }
        }
    }
    
}
