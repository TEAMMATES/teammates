package teammates.ui.webapi;

import teammates.common.exception.InvalidParametersException;

/**
 * Exception thrown when an HTTP parameter does not conform to an expected format
 * (e.g. passing a string when the expected parameter is a number).
 *
 * <p>This corresponds to HTTP 400 error.
 */
public class InvalidHttpParameterException extends RuntimeException {

    public InvalidHttpParameterException(String message) {
        super(message);
    }

    public InvalidHttpParameterException(InvalidParametersException cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidHttpParameterException(String message, InvalidHttpParameterException cause) {
        super(message, cause);
    }

    public InvalidHttpParameterException(String message, ArithmeticException cause) {
        super(message, cause);
    }

    public InvalidHttpParameterException(String message, NumberFormatException cause) {
        super(message, cause);
    }

    public InvalidHttpParameterException(String message, IllegalArgumentException cause) {
        super(message, cause);
    }

}
