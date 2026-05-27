package teammates.ui.exception;

/**
 * Exception thrown when an unexpected exception is caught, indicating a condition that should not occur
 * under normal assumptions.
 *
 * <p>This corresponds to HTTP 500 (Internal Server Error) response.
 */
public class UnexpectedServerException extends RuntimeException {
    public UnexpectedServerException(String message) {
        super(message);
    }

    public UnexpectedServerException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
