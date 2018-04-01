package teammates.common.exception;

@SuppressWarnings("serial")
public class InvalidPostParametersException extends RuntimeException {
    public InvalidPostParametersException(String message) {
        super(message);
    }

    public InvalidPostParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
