package teammates.test.driver;

/**
 * Abstract implementation of a {@link RetryableReturns} task for easy extending through anonymous classes.
 * @param <T> Result type.
 * @param <E> Throwable type.
 */
public abstract class RetryableTaskReturnsThrows<T, E extends Throwable> extends RetryableTaskThrows<E>
        implements RetryableReturns<T, E> {

    private T result;

    public RetryableTaskReturnsThrows(String name) {
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
