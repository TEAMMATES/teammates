package teammates.common.exception;

/**
 * Exception thrown when an entity is attempting to request for resources it does not have permission for.
 */
@SuppressWarnings("serial")
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

}
