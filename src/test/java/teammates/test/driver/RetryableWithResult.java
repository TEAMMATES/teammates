package teammates.test.driver;

public interface RetryableWithResult<ResultType> extends Retryable {
    public ResultType getResult();
}
