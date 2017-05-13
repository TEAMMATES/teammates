package teammates.test.driver;

public interface Retryable {
    public boolean run();
    public void beforeRetry();
}
