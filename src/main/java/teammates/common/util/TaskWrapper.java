package teammates.common.util;

import java.util.Map;

/**
 * Represents a task to be added to the task queue.
 */
public class TaskWrapper {

    private final String queueName;
    private final String workerUrl;
    private final Map<String, String> paramMap;
    private final Object requestBody;

    public TaskWrapper(String queueName, String workerUrl, Map<String, String> paramMap, Object requestBody) {
        this.queueName = queueName;
        this.workerUrl = workerUrl;
        this.paramMap = paramMap;
        this.requestBody = requestBody;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getWorkerUrl() {
        return workerUrl;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public Object getRequestBody() {
        return requestBody;
    }

}
