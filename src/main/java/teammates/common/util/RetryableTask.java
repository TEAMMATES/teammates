package teammates.common.util;

public class RetryableTask implements Retryable {
    @Override
    public boolean run() {
        return true;
    }
    @Override
    public void beforeRetry() {
    }
}
