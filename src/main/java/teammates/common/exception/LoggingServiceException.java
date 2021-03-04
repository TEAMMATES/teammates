package teammates.common.exception;

/**
 * Exception thrown when the logging service fails to create or retrieve logs.
 */
@SuppressWarnings("serial")
public class LoggingServiceException extends Exception {

    public LoggingServiceException(String message) {
        super(message);
    }

    public LoggingServiceException(Throwable cause) {
        super(cause);
    }

}
