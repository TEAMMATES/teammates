package teammates.common.exception;

/**
 * Exception thrown when an operation is determined to have exceeded the time it is allowed to run.
 */
public class DeadlineExceededException extends RuntimeException {

    public DeadlineExceededException(String message) {
        super(message);
    }
  
}
