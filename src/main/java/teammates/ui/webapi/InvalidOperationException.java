package teammates.ui.webapi;

import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InstructorUpdateException;

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

    public InvalidOperationException(EnrollException cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidOperationException(InstructorUpdateException cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidOperationException(String message, EntityAlreadyExistsException cause) {
        super(message, cause);
    }

}
