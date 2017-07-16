package teammates.common.util.retry;

/**
 * Convenience subclass of {link RetryableTaskThrows} for when checked exceptions are not thrown.
 */
public abstract class RetryableTask extends RetryableTaskThrows<RuntimeException> {
    public RetryableTask(String name) {
        super(name);
    }
}
