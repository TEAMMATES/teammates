package teammates.common.exception;

@SuppressWarnings("serial")
public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(){
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
}
