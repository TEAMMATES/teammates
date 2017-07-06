package teammates.common.util.retry;

/**
 * Abstract implementation of a {@link Retryable} task that returns a result, for easy extending through anonymous classes.
 * @param <T> Result type.
 * @param <E> Throwable type for signalling that the task should not be retried.
 */
public abstract class RetryableTaskReturnsThrows<T, E extends Throwable> extends Retryable<T, E> {

    private T result;

    public RetryableTaskReturnsThrows(String name) {
        super(name);
    }

    /**
     * Runs the task once and returns the result.
     */
    public abstract T run() throws E;

    @Override
    protected final T runExec() throws E {
        result = run();
        return result;
    }

    /**
     * Checks whether the task succeeded.
     */
    public boolean isSuccessful(T result) throws E {
        return true;
    }

    protected final T getResult() {
        return result;
    }

    /**
     * Checks whether the result is null.
     */
    public final boolean isResultNull() {
        return result == null;
    }

    @Override
    protected final boolean isSuccessfulExec() throws E {
        return isSuccessful(result);
    }
}
