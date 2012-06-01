package teammates.exception;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends TeammatesException {
	public EntityAlreadyExistsException(String message) {
		super(message);
	}
}
