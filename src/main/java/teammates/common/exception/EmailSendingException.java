package teammates.common.exception;

/**
 * Exception thrown when sending emails.
 */
@SuppressWarnings("serial")
public class EmailSendingException extends Exception {

    public EmailSendingException(Exception e) {
        super(e);
    }

}
