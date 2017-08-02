package teammates.common.util.retry;

@SuppressWarnings("serial")
public class MaximumRetriesExceededException extends Exception {

    /**
     * An optional final message updated by the task before its final failure.
     */
    public String finalMessage;

    /**
     * An optional final data object updated by the task before its final failure.
     */
    public Object finalData;

    public MaximumRetriesExceededException(Retryable task) {
        this(task, null);
    }

    public MaximumRetriesExceededException(Retryable task, Throwable cause) {
        super(task.getName() + " failed after maximum retries", cause);
        finalMessage = task.finalMessage;
        finalData = task.finalData;
    }
}
