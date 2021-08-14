package teammates.logic.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.TaskWrapper;

/**
 * Allows mocking of the {@link TaskQueuer} API used in production.
 *
 * <p>Instead of actually adding the task to the dev/production server's task queue,
 * the API will perform some operations that allow the queued tasks to be tracked.
 */
public class MockTaskQueuer extends TaskQueuer {

    private List<TaskWrapper> tasksAdded = new ArrayList<>();

    @Override
    void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap, Object requestBody,
                         long countdownTime) {
        // countdown time not tested
        TaskWrapper task = new TaskWrapper(queueName, workerUrl, paramMap, requestBody);
        tasksAdded.add(task);
    }

    /**
     * Gets the tasks added to the queue.
     */
    public List<TaskWrapper> getTasksAdded() {
        return tasksAdded;
    }

    /**
     * Gets the number of tasks added for each queue name.
     */
    public Map<String, Integer> getNumberOfTasksAdded() {
        Map<String, Integer> numberOfTasksAdded = new HashMap<>();
        for (TaskWrapper task : tasksAdded) {
            String queueName = task.getQueueName();

            int oldTaskCount = numberOfTasksAdded.getOrDefault(queueName, 0);
            numberOfTasksAdded.put(queueName, oldTaskCount + 1);
        }
        return numberOfTasksAdded;
    }

    /**
     * Clears the list of tasks added.
     */
    public void clearTasks() {
        tasksAdded.clear();
    }

}
