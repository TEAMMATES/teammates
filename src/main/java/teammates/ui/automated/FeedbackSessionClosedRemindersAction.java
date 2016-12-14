package teammates.ui.automated;

import teammates.logic.core.FeedbackSessionsLogic;

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
        FeedbackSessionsLogic.inst().scheduleFeedbackSessionClosedEmails();
    }
    
}
