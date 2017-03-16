package teammates.common.exception;

import java.util.List;

import teammates.common.util.StringHelper;

@SuppressWarnings("serial")
public class InvalidParametersException extends TeammatesException {
    public InvalidParametersException(String message) {
        super(message);
    }

    public InvalidParametersException(List<String> messages) {
        super(StringHelper.toString(messages));
    }

    public InvalidParametersException(String specificErrorcode, String message) {
        super(specificErrorcode, message);
    }

    public InvalidParametersException(Throwable cause) {
        super(cause);
    }

}
