package teammates.common.util;

import java.util.Map;

/**
 * Represents a task to be added to the task queue.
 */
public class TaskWrapper {

    private final String queueName;
    private final String workerUrl;
    private final Map<String, String[]> paramMap;

    public TaskWrapper(String queueName, String workerUrl, Map<String, String[]> paramMap) {
        this.queueName = queueName;
        this.workerUrl = workerUrl;
        this.paramMap = paramMap;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getWorkerUrl() {
        return workerUrl;
    }

    public Map<String, String[]> getParamMap() {
        return paramMap;
    }

}
