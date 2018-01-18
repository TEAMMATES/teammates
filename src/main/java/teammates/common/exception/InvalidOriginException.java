package teammates.common.exception;

@SuppressWarnings("serial")
public class InvalidOriginException extends RuntimeException {
    public InvalidOriginException() {
        super();
    }

    public InvalidOriginException(String message) {
        super(message);
    }
}
