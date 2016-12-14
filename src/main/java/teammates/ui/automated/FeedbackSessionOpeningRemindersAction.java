package teammates.ui.automated;

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
        FeedbackSessionsLogic.inst().scheduleFeedbackSessionOpeningEmails();
    }
    
}
