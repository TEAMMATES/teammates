package teammates.test.driver;

/**
 * Represents a {@link Retryable} task that returns a result.
 * @param <T> Result type.
 * @param <E> Throwable type.
 */
public interface RetryableReturns<T, E extends Throwable> extends Retryable<E> {

    /**
     * Returns the task result.
     */
    T getResult();
}
