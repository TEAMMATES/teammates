package teammates.common.exception;

@SuppressWarnings("serial")
public class EntityDoesNotExistException extends TeammatesException {
    public EntityDoesNotExistException(String message) {
        super(message);
    }

    public EntityDoesNotExistException(Throwable cause) {
        super(cause);
    }
}
