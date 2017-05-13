package teammates.common.util;

public class RetryableTask implements Retryable {
    protected String name;
    public RetryableTask(String name) {
        this.name = name;
    }
    @Override
    public boolean run() {
        return true;
    }
    @Override
    public void beforeRetry() {
    }
    @Override
    public String toString() {
        return name;
    }
}
