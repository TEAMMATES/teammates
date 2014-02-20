package teammates.logic.core;

import java.util.HashMap;
import java.util.logging.Logger;

import teammates.common.util.Utils;

public abstract class TaskQueueWorkerAction {
	protected static Logger log = Utils.getLogger();
	protected HashMap<String, String> paramMap;
	
	protected TaskQueueWorkerAction(HashMap<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	/**
	 * Method for executing the delegated task 
	 * @return return 0 on successful execution
	 * 		   and 1 on failed execution
	 */
	public abstract boolean execute();
}