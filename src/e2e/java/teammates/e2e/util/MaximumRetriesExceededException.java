package teammates.e2e.util;

/**
 * Exception thrown when a {@link Retryable} does not end successfully after exceeding the retry limits.
 */
@SuppressWarnings("serial")
public class MaximumRetriesExceededException extends Exception {

    public MaximumRetriesExceededException(Retryable task, Throwable cause) {
        super(task.getName() + " failed after maximum retries", cause);
    }

}
