package teammates.ui.automated;

import teammates.logic.core.FeedbackSessionsLogic;

public class FeedbackSessionPublishedRemindersAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return "send published reminders";
    }
    
    @Override
    protected String getActionMessage() {
        return "Generating reminders for published feedback sessions.";
    }
    
    @Override
    public void execute() {
        FeedbackSessionsLogic.inst().scheduleFeedbackSessionPublishedEmails();
    }
    
}
