package teammates.common.exception;

/**
 * Exception thrown when the logs service fails to create or retrieve logs.
 */
@SuppressWarnings("serial")
public class LogsServiceException extends Exception {

    public LogsServiceException(String message) {
        super(message);
    }

    public LogsServiceException(Throwable cause) {
        super(cause);
    }

}
