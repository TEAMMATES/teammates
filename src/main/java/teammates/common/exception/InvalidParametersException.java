package teammates.common.exception;

import java.util.List;

import teammates.common.util.StringHelper;

@SuppressWarnings("serial")
public class InvalidParametersException extends TeammatesException {
    public InvalidParametersException(final String message) {
        super(message);
    }

    public InvalidParametersException(final List<String> messages) {
        super(StringHelper.toString(messages));
    }

    public InvalidParametersException(final String specificErrorcode, final String message) {
        super(specificErrorcode, message);
    }
}
