package teammates.common.util.retry;

/**
 * Exception thrown when a {@link Retryable} does not end successfully after exceeding the retry limits.
 */
@SuppressWarnings("serial")
public class MaximumRetriesExceededException extends Exception {

    /**
     * An optional final message updated by the task before its final failure.
     */
    public final String finalMessage;

    /**
     * An optional final data object updated by the task before its final failure.
     */
    public final Object finalData;

    public MaximumRetriesExceededException(Retryable task) {
        this(task, null);
    }

    public MaximumRetriesExceededException(Retryable task, Throwable cause) {
        super(task.getName() + " failed after maximum retries", cause);
        this.finalMessage = task.finalMessage;
        this.finalData = task.finalData;
    }

}
