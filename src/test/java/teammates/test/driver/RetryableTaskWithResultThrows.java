package teammates.test.driver;

/**
 * Default implementation of a {@link RetryableWithResult} task for easy extending through anonymous classes.
 * @param <T> Result type.
 */
public class RetryableTaskWithResultThrows<T, E extends Throwable> extends RetryableTaskThrows<E> implements RetryableWithResult<T, E> {

    private T result;

    public RetryableTaskWithResultThrows(String name) {
        super(name);
    }

    @Override
    public T getResult() {
        return result;
    }

    protected void setResult(T result) {
        this.result = result;
    }
}
