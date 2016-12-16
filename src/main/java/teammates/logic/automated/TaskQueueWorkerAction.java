package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;

import teammates.common.util.Logger;

public abstract class TaskQueueWorkerAction {
    protected static final Logger log = Logger.getLogger();
    protected HttpServletRequest request;
    
    protected TaskQueueWorkerAction(HttpServletRequest request) {
        this.request = request;
    }
    
    /**
     * Method for executing the delegated task
     * @return return 0 on successful execution
     *            and 1 on failed execution
     */
    public abstract boolean execute();
}
