package teammates.common.exception;

@SuppressWarnings("serial")
public class EmailSendingException extends RuntimeException {
    
    public EmailSendingException(Exception e) {
        super(e.getMessage());
    }
    
}
