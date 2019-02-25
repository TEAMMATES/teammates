package teammates.common.exception;

/**
 * Exception thrown due to attempting to create an entity that has existed.
 */
@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends Exception {

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

}
