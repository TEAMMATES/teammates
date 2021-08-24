package teammates.ui.webapi;

/**
 * Exception thrown when an HTTP parameter does not conform to an expected format
 * (e.g. passing a string when the expected parameter is a number).
 */
public class InvalidHttpParameterException extends RuntimeException {

    public InvalidHttpParameterException(String message) {
        super(message);
    }

    public InvalidHttpParameterException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public InvalidHttpParameterException(String message, Throwable cause) {
        super(message, cause);
    }

}
