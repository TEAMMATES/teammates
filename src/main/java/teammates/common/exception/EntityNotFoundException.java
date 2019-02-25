package teammates.common.exception;

/**
 * Runtime exception wrapper for {@link EntityDoesNotExistException}.
 */
@SuppressWarnings("serial")
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(EntityDoesNotExistException e) {
        super(e.getMessage());
    }

}
