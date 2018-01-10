package teammates.test.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.TaskWrapper;
import teammates.logic.api.TaskQueuer;

/**
 * Allows mocking of the {@link TaskQueuer} API used in production.
 *
 * <p>Instead of actually adding the task to the dev/production server's task queue,
 * the API will perform some operations that allow the queued tasks to be tracked.
 */
public class MockTaskQueuer extends TaskQueuer {

    private List<TaskWrapper> tasksAdded = new ArrayList<>();

    @Override
    protected void addTask(String queueName, String workerUrl, Map<String, String> paramMap) {
        Map<String, String[]> multisetParamMap = new HashMap<>();
        paramMap.forEach((key, value) -> multisetParamMap.put(key, new String[] { value }));
        TaskWrapper task = new TaskWrapper(queueName, workerUrl, multisetParamMap);
        tasksAdded.add(task);
    }

    @Override
    protected void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap,
                                   long countdownTime) {
        // countdown time not tested, thus fallback to another method
        addTask(queueName, workerUrl, paramMap);
    }

    @Override
    protected void addTaskMultisetParam(String queueName, String workerUrl, Map<String, String[]> paramMap) {
        TaskWrapper task = new TaskWrapper(queueName, workerUrl, paramMap);
        tasksAdded.add(task);
    }

    @Override
    public List<TaskWrapper> getTasksAdded() {
        return tasksAdded;
    }

    @Override
    public Map<String, Integer> getNumberOfTasksAdded() {
        Map<String, Integer> numberOfTasksAdded = new HashMap<>();
        for (TaskWrapper task : tasksAdded) {
            String queueName = task.getQueueName();

            int oldTaskCount = numberOfTasksAdded.getOrDefault(queueName, 0);
            numberOfTasksAdded.put(queueName, oldTaskCount + 1);
        }
        return numberOfTasksAdded;
    }

}
