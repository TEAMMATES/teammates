package teammates.common.exception;

import java.util.List;

import teammates.common.util.StringHelper;

/**
 * Exception thrown when non-HTTP parameter validation fails.
 */
@SuppressWarnings("serial")
public class InvalidParametersException extends Exception {

    public InvalidParametersException(String message) {
        super(message);
    }

    public InvalidParametersException(List<String> messages) {
        super(StringHelper.toString(messages));
    }

    public InvalidParametersException(Throwable cause) {
        super(cause);
    }

}
