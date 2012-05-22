package teammates.exception;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends Exception {
	public EntityAlreadyExistsException() {
		super("Course exists for the coordinator already.");
	}
}
