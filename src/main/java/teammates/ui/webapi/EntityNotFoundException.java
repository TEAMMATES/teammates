package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;

/**
 * Exception thrown when a requested entity is not found.
 *
 * <p>More often than not, this will be a runtime exception wrapper for {@link EntityDoesNotExistException}.
 *
 * <p>This corresponds to HTTP 404 error.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(EntityDoesNotExistException e) {
        super(e.getMessage(), e);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, EntityDoesNotExistException e) {
        super(message, e);
    }

}
