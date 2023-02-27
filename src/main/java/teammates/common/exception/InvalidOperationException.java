package teammates.common.exception;

/**
 * Exception thrown when the requested operation
 *  is invalid and cannot be performed.
 */
public class InvalidOperationException extends Exception {

    public InvalidOperationException(String message) {
        super(message);
    }
}
