package teammates.common.util.retry;

@SuppressWarnings("serial")
public class MaximumRetriesExceededException extends RuntimeException {

    public Object finalResult;

    public MaximumRetriesExceededException(Retryable task) {
        super(task.getName() + " failed after maximum retries" + task.finalMessage == null ? "" : ": " + task.finalMessage);
        if (task instanceof RetryableTaskReturnsThrows) {
            finalResult = ((RetryableTaskReturnsThrows) task).getResult();
        }
    }
}
