package teammates.exception;

@SuppressWarnings("serial")
public class TeammatesException extends Exception {

	public String errorCode;

	public TeammatesException() {
		super();
	}

	public TeammatesException(String message) {
		super(message);
	}

	public TeammatesException(String errorcode,	String message) {
		super(message);
		errorCode = errorcode;
	}

}