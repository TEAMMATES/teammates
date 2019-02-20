package teammates.common.util;

/**
 * Encapsulates the status of sending an email.
 */
public class EmailSendingStatus {

    private final int statusCode;
    private final String message;
    private final boolean isSuccess;

    public EmailSendingStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.isSuccess = statusCode >= 200 && statusCode <= 299;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

}
