package teammates.test.driver;

/**
 * Convenience subclass of {link RetryableTaskWithResultThrows} for when checked exceptions are not thrown.
 * @param <T> Result type.
 */
public class RetryableTaskWithResult<T> extends RetryableTaskWithResultThrows<T, RuntimeException> {
    public RetryableTaskWithResult(String name) {
        super(name);
    }
}
