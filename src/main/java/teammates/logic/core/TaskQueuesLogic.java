package teammates.logic.core;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Handles  operations related to Task Queues.
 */
public class TaskQueuesLogic {

    private static TaskQueuesLogic instance = null;
    public static TaskQueuesLogic inst() {
        if (instance == null){
            instance = new TaskQueuesLogic();
        }
        return instance;
    }
    
    public void createAndAddTask(String queueName, 
            String workerUrl, HashMap<String, String> paramMap) {
        Queue requiredQueue = QueueFactory.getQueue(queueName);
        TaskOptions taskToBeAdded = TaskOptions.Builder.withUrl(workerUrl);
        
        for(Map.Entry<String, String> entry : paramMap.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            
            taskToBeAdded = taskToBeAdded.param(name, value);
        }
        
        requiredQueue.add(taskToBeAdded);
    }
    
    // TODO Combine this and createAndAddTask and modify task schedulers accordingly?
    public void createAndAddTaskMultisetParam(String queueName, 
            String workerUrl, HashMap<String, String[]> paramMap) {
        Queue requiredQueue = QueueFactory.getQueue(queueName);
        TaskOptions taskToBeAdded = TaskOptions.Builder.withUrl(workerUrl);
        
        for(Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String name = entry.getKey();
            String[] value = entry.getValue();
            
            for (int i = 0; i < value.length; i++) {
                taskToBeAdded = taskToBeAdded.param(name, value[i]);
            }
        }
        
        requiredQueue.add(taskToBeAdded);
    }
    
    public void createAndAddDeferredTask(String queueName, 
            String workerUrl, HashMap<String, String> paramMap, long countdownTime) {
        Queue requiredQueue = QueueFactory.getQueue(queueName);
        TaskOptions taskToBeAdded = TaskOptions.Builder.withUrl(workerUrl);
        taskToBeAdded.countdownMillis(countdownTime);
        
        for(Map.Entry<String, String> entry : paramMap.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            
            taskToBeAdded = taskToBeAdded.param(name, value);
        }
        
        requiredQueue.add(taskToBeAdded);
    }
}
