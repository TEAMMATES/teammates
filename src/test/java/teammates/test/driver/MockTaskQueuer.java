package teammates.test.driver;

import java.util.HashMap;
import java.util.Map;

import teammates.logic.api.TaskQueuer;

/**
 * Allows mocking of the {@link TaskQueuer} API used in production.
 * 
 * Instead of actually adding the task to the dev/production server's task queue,
 * the API will perform some operations that allow the queued tasks to be tracked.
 */
public class MockTaskQueuer extends TaskQueuer {
    
    private Map<String, Integer> tasksAdded = new HashMap<String, Integer>();
    
    @Override
    protected void addTask(String queueName, String workerUrl, Map<String, String> paramMap) {
        addTask(queueName);
    }
    
    @Override
    protected void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap,
                                   long countdownTime) {
        addTask(queueName);
    }
    
    @Override
    protected void addTaskMultisetParam(String queueName, String workerUrl, Map<String, String[]> paramMap) {
        addTask(queueName);
    }
    
    private void addTask(String queueName) {
        if (!tasksAdded.containsKey(queueName)) {
            tasksAdded.put(queueName, 0);
        }
        int oldTaskCount = tasksAdded.get(queueName);
        tasksAdded.put(queueName, oldTaskCount + 1);
    }
    
    @Override
    public Map<String, Integer> getTasksAdded() {
        return tasksAdded;
    }
    
}
