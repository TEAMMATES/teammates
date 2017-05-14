package teammates.test.driver;

/**
 * Abstract implementation of a {@link RetryableWithResult} task for easy extending through anonymous classes.
 * @param <T> Result type.
 * @param <E> Throwable type.
 */
public abstract class RetryableTaskWithResultThrows<T, E extends Throwable> extends RetryableTaskThrows<E>
        implements RetryableWithResult<T, E> {

    private T result;

    public RetryableTaskWithResultThrows(String name) {
        super(name);
    }

    @Override
    public T getResult() {
        return result;
    }

    /**
     * Sets the result that is returned by {@code run()}.
     */
    protected void setResult(T result) {
        this.result = result;
    }
}
