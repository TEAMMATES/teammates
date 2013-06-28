package teammates.common.exception;

import java.util.List;

import teammates.common.Common;


@SuppressWarnings("serial")
public class InvalidParametersException extends TeammatesException {

	public InvalidParametersException(String message) {
		super(message);
	}
	
	public InvalidParametersException(List<String> messages) {
		super(Common.toString(messages));
	}

	public InvalidParametersException(String specificErrorcode,	String message) {
		super(specificErrorcode, message);
	}

}
