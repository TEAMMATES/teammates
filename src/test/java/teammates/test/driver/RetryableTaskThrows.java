package teammates.test.driver;

/**
 * Default implementation of a {@link Retryable} task for easy extending through anonymous classes.
 */
public class RetryableTaskThrows<E extends Throwable> implements Retryable<E> {

    protected String name;

    public RetryableTaskThrows(String name) {
        this.name = name;
    }

    @Override
    public boolean run() throws E {
        return true;
    }

    @Override
    public void beforeRetry() throws E {
        // Default implementation does nothing.
    }

    @Override
    public String toString() {
        return name;
    }
}
