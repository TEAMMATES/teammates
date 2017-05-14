package teammates.test.driver;

public class RetryableTask extends RetryableTaskThrows<RuntimeException> {
    public RetryableTask(String name) {
        super(name);
    }
}
