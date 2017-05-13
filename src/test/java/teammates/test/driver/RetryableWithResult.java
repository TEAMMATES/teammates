package teammates.test.driver;

/**
 * Represents a {@link Retryable} task that returns a result.
 * @param <T> Result type.
 */
public interface RetryableWithResult<T> extends Retryable {

    /**
     * Returns the task result.
     */
    T getResult();
}
