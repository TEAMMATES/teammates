package teammates.common.exception;

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
