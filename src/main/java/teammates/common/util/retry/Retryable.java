package teammates.common.util.retry;

/**
 * Represents a task that can be retried.
 * @param <T> Result type.
 * @param <E> Throwable type for signalling that the task should not be retried.
 */
public abstract class Retryable<T, E extends Throwable> {

    protected String name;

    /**
     * An optional final message to show in the {@link MaximumRetriesExceededException} thrown
     * should the task fail after maximum retries.
     */
    protected String finalMessage;

    /**
     * An optional final object to embed in the {@link MaximumRetriesExceededException} thrown
     * should the task fail after maximum retries.
     */
    protected Object finalData;

    public Retryable(String name) {
        this.name = name;
    }

    /**
     * Executes a method that runs the task once and returns the result.
     */
    protected abstract T runExec() throws E;

    /**
     * Executes a method that checks whether the task succeeded.
     */
    protected abstract boolean isSuccessfulExec() throws E;

    /**
     * Performs additional steps required before each retry of the task.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void beforeRetry() throws E {
        // Does nothing by default so that it can be skipped entirely in anonymous classes when not used.
    }

    /**
     * Returns the name of the task.
     */
    public String getName() {
        return name;
    }
}
