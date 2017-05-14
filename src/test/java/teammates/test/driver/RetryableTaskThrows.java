package teammates.test.driver;

/**
 * Abstract implementation of a {@link Retryable} task for easy extending through anonymous classes.
 * @param <E> Throwable type.
 */
public abstract class RetryableTaskThrows<E extends Throwable> implements Retryable<E> {

    protected String name;

    public RetryableTaskThrows(String name) {
        this.name = name;
    }

    @Override
    public abstract boolean run() throws E;

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public void beforeRetry() throws E {
        // Does nothing by default so that it can be skipped entirely in anonymous classes when not used.
    }

    @Override
    public String toString() {
        return name;
    }
}
