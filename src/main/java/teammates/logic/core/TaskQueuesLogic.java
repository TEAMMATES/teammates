package teammates.logic.core;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Handles operations related to task queues.
 */
public class TaskQueuesLogic {
    
    /**
     * Adds the given task to the specified queue.
     * 
     * @param queueName the name of the queue for the task
     * @param workerUrl the URL to be triggered when the task is executed
     * @param paramMap the one-to-one parameter mapping for the task
     */
    public void addTask(String queueName, String workerUrl, Map<String, String> paramMap) {
        addDeferredTask(queueName, workerUrl, paramMap, 0);
    }
    
    /**
     * Adds the given task, to be run after the specified time, to the specified queue.
     * 
     * @param queueName the name of the queue for the task
     * @param workerUrl the URL to be triggered when the task is executed
     * @param paramMap the one-to-one parameter mapping for the task
     * @param countdownTime the time delay for the task to be executed
     */
    public void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap, long countdownTime) {
        Map<String, String[]> multisetParamMap = new HashMap<String, String[]>();
        for (Map.Entry<String, String> entrySet : paramMap.entrySet()) {
            multisetParamMap.put(entrySet.getKey(), new String[] { entrySet.getValue() });
        }
        addDeferredTaskMultisetParam(queueName, workerUrl, multisetParamMap, countdownTime);
    }
    
    /**
     * Adds the given task to the specified queue.
     * 
     * @param queueName the name of the queue for the task
     * @param workerUrl the URL to be triggered when the task is executed
     * @param paramMap the one-to-many parameter mapping for the task
     */
    public void addTaskMultisetParam(String queueName, String workerUrl, Map<String, String[]> paramMap) {
        addDeferredTaskMultisetParam(queueName, workerUrl, paramMap, 0);
    }
    
    /**
     * Adds the given task, to be run after the specified time, to the specified queue.
     * 
     * @param queueName the name of the queue for the task
     * @param workerUrl the URL to be triggered when the task is executed
     * @param paramMap the one-to-many parameter mapping for the task
     * @param countdownTime the time delay for the task to be executed
     */
    public void addDeferredTaskMultisetParam(
            String queueName, String workerUrl, Map<String, String[]> paramMap, long countdownTime) {
        Queue requiredQueue = QueueFactory.getQueue(queueName);
        TaskOptions taskToBeAdded = TaskOptions.Builder.withUrl(workerUrl);
        if (countdownTime > 0) {
            taskToBeAdded.countdownMillis(countdownTime);
        }
        
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            
            for (String value : values) {
                taskToBeAdded = taskToBeAdded.param(name, value);
            }
        }
        
        requiredQueue.add(taskToBeAdded);
    }
    
}
