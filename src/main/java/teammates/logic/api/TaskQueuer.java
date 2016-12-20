package teammates.logic.api;

import java.util.Map;

import teammates.logic.core.TaskQueuesLogic;

/**
 * Allows for adding specific type of tasks to the task queue.
 */
public class TaskQueuer {
    
    // The following methods are facades to the actual logic for adding tasks to the queue.
    // Using this method, the actual logic can still be black-boxed
    // while at the same time allowing this API to be mocked during test.
    
    protected void addTask(String queueName, String workerUrl, Map<String, String> paramMap) {
        new TaskQueuesLogic().createAndAddTask(queueName, workerUrl, paramMap);
    }
    
    protected void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap,
                                   long countdownTime) {
        new TaskQueuesLogic().createAndAddDeferredTask(queueName, workerUrl, paramMap, countdownTime);
    }
    
    protected void addTaskMultisetParam(String queueName, String workerUrl, Map<String, String[]> paramMap) {
        new TaskQueuesLogic().createAndAddTaskMultisetParam(queueName, workerUrl, paramMap);
    }
    
    // The following methods are the actual API methods to be used by the client classes
    
}
