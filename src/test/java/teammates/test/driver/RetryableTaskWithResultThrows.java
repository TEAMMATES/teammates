package teammates.test.driver;

/**
 * Default implementation of a {@link RetryableWithResult} task for easy extending through anonymous classes.
 * @param <T> Result type.
 */
public class RetryableTaskWithResult<T, E extends Throwable> extends RetryableTask<E> implements RetryableWithResult<T, E> {

    private T result;

    public RetryableTaskWithResult(String name) {
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
