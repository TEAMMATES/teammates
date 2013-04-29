package teammates.common.exception;


@SuppressWarnings("serial")
public class InvalidParametersException extends TeammatesException {

	public InvalidParametersException(String message) {
		super(message);
	}

	public InvalidParametersException(String specificErrorcode,	String message) {
		super(specificErrorcode, message);
	}
}
