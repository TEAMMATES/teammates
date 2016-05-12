package teammates.common.exception;

@SuppressWarnings("serial")
public class EntityDoesNotExistException extends Exception {
    public EntityDoesNotExistException(final String message) {
        super(message);
    }
}
