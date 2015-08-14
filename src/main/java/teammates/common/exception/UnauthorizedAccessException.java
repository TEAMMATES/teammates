package teammates.common.exception;

@SuppressWarnings("serial")
public class UnauthorizedAccessException extends RuntimeException {
    
    // To display an error message on unauthorized.jsp or not
    private boolean displayErrorMessage;
    
    public UnauthorizedAccessException(){
        super();
        this.displayErrorMessage = false;
    }

    public UnauthorizedAccessException(String message) {
        super(message);
        this.displayErrorMessage = false;
    }
    
    public UnauthorizedAccessException(String message, boolean displayErrorMessage) {
        super(message);
        this.displayErrorMessage = displayErrorMessage;
    }
    
    public boolean isDisplayErrorMessage() {
        return displayErrorMessage;
    }
}
