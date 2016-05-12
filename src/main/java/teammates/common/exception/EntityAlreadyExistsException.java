package teammates.common.exception;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends TeammatesException {
    
    public Object existingEntity;
    
    public EntityAlreadyExistsException(final String message) {
        super(message);
    }
    
    public EntityAlreadyExistsException(final String message, final Object entity) {
        this(message);
        this.existingEntity = entity;
    }
}
