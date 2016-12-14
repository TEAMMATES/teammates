package teammates.ui.automated;

/**
 * An automated "action" to be performed by the system, triggered by cron jobs or task queues.
 * Non-administrators are barred from performing this class of action.
 */
public abstract class AutomatedAction {
    
    protected abstract String getActionDescription();
    
    protected abstract String getActionMessage();
    
    public abstract void execute();
    
}
