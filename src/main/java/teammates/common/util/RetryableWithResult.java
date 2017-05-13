package teammates.common.util;

public interface RetryableWithResult<ResultType> extends Retryable {
    public ResultType getResult();
}
