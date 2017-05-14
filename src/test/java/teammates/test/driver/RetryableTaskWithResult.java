package teammates.test.driver;

public class RetryableTaskWithResult<T> extends RetryableTaskWithResultThrows<T, RuntimeException> {
    public RetryableTaskWithResult(String name) {
        super(name);
    }
}
