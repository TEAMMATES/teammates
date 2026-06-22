package teammates.ui.exception;

import teammates.common.exception.EntityAlreadyExistsException;

/**
 * Exception thrown when a normally valid operation is not valid due to factors outside of the operation itself,
 * e.g. when trying to insert duplicate entry into the database.
 *
 * <p>This corresponds to HTTP 409 error.
 */
public class InvalidOperationException extends Exception {

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(EntityAlreadyExistsException cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidOperationException(Exception cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidOperationException(String message, EntityAlreadyExistsException cause) {
        super(message, cause);
    }

}
