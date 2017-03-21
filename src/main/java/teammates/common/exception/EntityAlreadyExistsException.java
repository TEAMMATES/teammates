package teammates.common.exception;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends TeammatesException {

    public Object existingEntity;

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(String message, Object entity) {
        this(message);
        this.existingEntity = entity;
    }
}
