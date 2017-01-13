package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * Cron job: schedules feedback session opening emails to be sent.
 */
public class FeedbackSessionOpeningRemindersAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return "send opening reminders";
    }
    
    @Override
    protected String getActionMessage() {
        return "Generating reminders for opening feedback sessions.";
    }
    
    @Override
    public void execute() {
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        List<FeedbackSessionAttributes> sessions = fsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        
        for (FeedbackSessionAttributes session : sessions) {
            List<EmailWrapper> emailsToBeSent = new EmailGenerator().generateFeedbackSessionOpeningEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                session.setSentOpenEmail(true);
                fsLogic.updateFeedbackSession(session);
            } catch (Exception e) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
            }
        }
    }
    
}
