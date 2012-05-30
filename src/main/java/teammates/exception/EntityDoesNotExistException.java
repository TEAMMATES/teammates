package teammates.exception;

@SuppressWarnings("serial")
public class EntityDoesNotExistException extends Exception {

	public EntityDoesNotExistException(String message) {
		super(message);
	}

}
