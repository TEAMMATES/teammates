package teammates.test.driver;

public class RetryableTaskUnchecked extends RetryableTask<RuntimeException> {
    public RetryableTaskUnchecked(String name) {
        super(name);
    }
}
