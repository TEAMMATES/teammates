package teammates.test.driver;

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
     * Runs the task once and returns the result.
     */
    // CHECKSTYLE.OFF:MethodName internal variable underscore naming convention applied to abstract methods
    protected abstract T run_() throws E;

    /**
     * Checks whether the task succeeded.
     */
    protected abstract boolean isSuccessful_() throws E;
    // CHECKSTYLE.OFF:MethodName

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
