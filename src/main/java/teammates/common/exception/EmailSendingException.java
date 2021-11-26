package teammates.common.exception;

/**
 * Exception thrown when error is encountered while sending email.
 */
public class EmailSendingException extends Exception {

    private final int statusCode;

    public EmailSendingException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
