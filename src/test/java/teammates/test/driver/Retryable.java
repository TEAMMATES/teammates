package teammates.test.driver;

/**
 * Represents a task that can be retried and throws a throwable.
 * @param <E> Throwable type.
 */
public interface Retryable<E extends Throwable> {

    /**
     * Runs the task once and checks its status.
     * Returns true if task ran successfully; false otherwise.
     */
    boolean run() throws E;

    /**
     * Performs additional steps required before each retry of the task.
     */
    void beforeRetry() throws E;
}
