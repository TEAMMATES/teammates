package teammates.common.util.retry;

/**
 * Convenience subclass of {link RetryableTaskReturnsThrows} for when checked exceptions are not thrown.
 * @param <T> Result type.
 */
public abstract class RetryableTaskReturns<T> extends RetryableTaskReturnsThrows<T, RuntimeException> {
    public RetryableTaskReturns(String name) {
        super(name);
    }
}
