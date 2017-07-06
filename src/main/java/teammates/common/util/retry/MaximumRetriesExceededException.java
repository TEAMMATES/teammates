package teammates.common.util.retry;

@SuppressWarnings("serial")
public class MaximumRetriesExceededException extends Exception {

    public Object finalResult;

    public MaximumRetriesExceededException(Retryable task) {
        this(task, null);
    }

    public MaximumRetriesExceededException(Retryable task, Throwable cause) {
        super(task.getName() + " failed after maximum retries" + task.finalMessage == null ? "" : ": " + task.finalMessage,
                cause);
        if (task instanceof RetryableTaskReturnsThrows) {
            finalResult = ((RetryableTaskReturnsThrows) task).getResult();
        }
    }
}
