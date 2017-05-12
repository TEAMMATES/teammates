package teammates.logic.core;

import java.util.Map;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import teammates.common.util.TaskWrapper;

/**
 * Handles operations related to task queues.
 */
public class TaskQueuesLogic {

    /**
     * Adds the given task to the specified queue.
     *
     * @param task the task object containing the details of task to be added
     */
    public void addTask(TaskWrapper task) {
        addDeferredTask(task, 0);
    }

    /**
     * Adds the given task, to be run after the specified time, to the specified queue.
     *
     * @param task the task object containing the details of task to be added
     * @param countdownTime the time delay for the task to be executed
     */
    public void addDeferredTask(TaskWrapper task, long countdownTime) {
        Queue requiredQueue = QueueFactory.getQueue(task.getQueueName());
        TaskOptions taskToBeAdded = TaskOptions.Builder.withUrl(task.getWorkerUrl());
        if (countdownTime > 0) {
            taskToBeAdded.countdownMillis(countdownTime);
        }

        for (Map.Entry<String, String[]> entry : task.getParamMap().entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();

            for (String value : values) {
                taskToBeAdded = taskToBeAdded.param(name, value);
            }
        }

        requiredQueue.add(taskToBeAdded);
    }

}
