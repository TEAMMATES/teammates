package teammates.test.driver;

/**
 * Represents a task that can be retried.
 */
public interface Retryable<E extends Throwable> {

    /**
     * Run the task once and check its status.
     * Returns true if task ran successfully; false otherwise.
     */
    boolean run() throws E;

    /**
     * Performs additional steps required before each retry of the task.
     */
    void beforeRetry() throws E;
}
