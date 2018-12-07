package teammates.common.exception;

@SuppressWarnings("serial")
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(EntityDoesNotExistException e) {
        super(e.getMessage());
    }

}
