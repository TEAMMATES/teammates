package teammates.common.exception;

/**
 * Exception thrown due to attempting to operate on an entity that does not exist.
 */
@SuppressWarnings("serial")
public class EntityDoesNotExistException extends Exception {

    public EntityDoesNotExistException(String message) {
        super(message);
    }

}
