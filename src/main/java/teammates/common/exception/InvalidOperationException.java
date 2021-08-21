package teammates.common.exception;

/**
 * Exception thrown when a normally valid operation is not valid due to factors outside of the operation itself,
 * e.g. when trying to insert duplicate entry into the database.
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
