package teammates.exception;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends Exception {
	public EntityAlreadyExistsException(String message) {
		super(message);
	}
}
