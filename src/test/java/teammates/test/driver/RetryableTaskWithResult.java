package teammates.test.driver;

public class RetryableTaskWithResultUnchecked<T> extends RetryableTaskWithResult<T, RuntimeException> {
    public RetryableTaskWithResultUnchecked(String name) {
        super(name);
    }
}
