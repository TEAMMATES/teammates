package teammates.common.util;

public class RetryableTaskWithResult<ResultType> extends RetryableTask implements RetryableWithResult<ResultType> {
    private ResultType result;
    public RetryableTaskWithResult(String name) {
        super(name);
    }
    @Override
    public ResultType getResult() {
        return result;
    }
    public void setResult(ResultType result) {
        this.result = result;
    }
}
