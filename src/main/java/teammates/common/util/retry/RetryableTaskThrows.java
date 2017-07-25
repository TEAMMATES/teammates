package teammates.common.util.retry;

/**
 * Abstract implementation of a {@link Retryable} task, for easy extending through anonymous classes.
 * @param <E> Throwable type for signalling that the task should not be retried.
 */
public abstract class RetryableTaskThrows<E extends Throwable> extends Retryable<Void, E> {

    public RetryableTaskThrows(String name) {
        super(name);
    }

    /**
     * Runs the task once.
     */
    public abstract void run() throws E;

    @Override
    protected final Void runExec() throws E {
        run();
        return null;
    }

    /**
     * Checks whether the task succeeded.
     */
    public boolean isSuccessful() throws E {
        return true;
    }

    @Override
    protected final boolean isSuccessfulExec() throws E {
        return isSuccessful();
    }
}
