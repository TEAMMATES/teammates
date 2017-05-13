package teammates.common.util;

public interface Retryable {
    public boolean run();
    public void beforeRetry();
}
