package teammates.common.exception;

@SuppressWarnings("serial")
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(){
        super();
    }

    public UnauthorizedAccessException(final String message) {
        super(message);
    }
}
