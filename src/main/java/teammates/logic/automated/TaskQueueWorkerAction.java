package teammates.logic.automated;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import teammates.common.util.Utils;

public abstract class TaskQueueWorkerAction {
    protected static Logger log = Utils.getLogger();
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