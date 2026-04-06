package teammates.common.exception;
/**
 * Exception thrown when an unexpected server-side error occurs.
 * This is used for centralized error handling and maps to HTTP 500 responses.
 */
public class UnexpectedServerException extends RuntimeException {

    public UnexpectedServerException(String message) {
        super(message);
    }

    public UnexpectedServerException(Throwable cause) {
        super(cause);
    }

    public UnexpectedServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
