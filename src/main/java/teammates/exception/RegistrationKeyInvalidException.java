package teammates.exception;

@SuppressWarnings("serial")
public class RegistrationKeyInvalidException extends Exception {
	public RegistrationKeyInvalidException() {
		super("Registration key is not valid.");
	}
	
	public RegistrationKeyInvalidException(String message) {
		super("Registration key is not valid."+message);
	}

}
