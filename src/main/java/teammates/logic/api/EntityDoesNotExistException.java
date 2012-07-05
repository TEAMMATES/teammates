package teammates.logic.api;

@SuppressWarnings("serial")
public class EntityDoesNotExistException extends Exception {

	public EntityDoesNotExistException(String message) {
		super(message);
	}

}
