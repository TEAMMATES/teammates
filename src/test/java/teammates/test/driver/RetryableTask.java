package teammates.test.driver;

/**
 * Default implementation of a {@link Retryable} task for easy extending through anonymous classes.
 */
public class RetryableTask implements Retryable {

    protected String name;

    public RetryableTask(String name) {
        this.name = name;
    }

    @Override
    public boolean run() {
        return true;
    }

    @Override
    public void beforeRetry() {
        // Default implementation does nothing.
    }

    @Override
    public String toString() {
        return name;
    }
}
