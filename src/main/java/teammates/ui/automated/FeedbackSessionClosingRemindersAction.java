package teammates.ui.automated;

import teammates.logic.core.FeedbackSessionsLogic;

public class FeedbackSessionClosingRemindersAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return "send closing reminders";
    }
    
    @Override
    protected String getActionMessage() {
        return "Generating reminders for closing feedback sessions.";
    }
    
    @Override
    public void execute() {
        FeedbackSessionsLogic.inst().scheduleFeedbackSessionClosingEmails();
    }
    
}
