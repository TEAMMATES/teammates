package teammates.common.exception;

import teammates.common.datatransfer.EntityAttributes;


@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends TeammatesException {
    private EntityAttributes offendingEntity;
    
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
    
    public EntityAlreadyExistsException(String message, EntityAttributes obj) {
        super(message);
        offendingEntity = obj;
    }
    
    public EntityAttributes getOffendingEntity() {
        return offendingEntity;
    }
}
