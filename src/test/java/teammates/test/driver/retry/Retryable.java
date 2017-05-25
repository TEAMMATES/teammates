package teammates.test.driver.retry;

/**
 * Represents a task that can be retried.
 * @param <T> Result type.
 * @param <E> Throwable type.
 */
public abstract class Retryable<T, E extends Throwable> {

    protected String name;

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
