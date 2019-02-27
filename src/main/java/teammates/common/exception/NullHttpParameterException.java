package teammates.common.exception;

/**
 * Exception thrown when an HTTP parameter is expected to be present but is actually absent.
 */
@SuppressWarnings("serial")
public class NullHttpParameterException extends InvalidHttpParameterException {

    public NullHttpParameterException(String message) {
        super(message);
    }

}
