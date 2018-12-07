package teammates.logic.core;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.EmailWrapper;

/**
 * An email sender interface used by services for sending emails.
 */
public abstract class EmailSenderService {

    /**
     * Parses the {@code wrapper} email object to specific implementations of email object
     * used by the service.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // accounts for the many different Exceptions from different email services
    public abstract Object parseToEmail(EmailWrapper wrapper) throws Exception;

    /**
     * Sends the email packaged as a {@code wrapper}.
     */
    public void sendEmail(EmailWrapper wrapper) throws EmailSendingException {
        try {
            sendEmailWithService(wrapper);
        } catch (Exception e) {
            throw new EmailSendingException(e);
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // accounts for the many different Exceptions from different email services
    protected abstract void sendEmailWithService(EmailWrapper wrapper) throws Exception;

    static boolean isNotSuccessStatus(int statusCode) {
        return statusCode < 200 || statusCode > 299;
    }

}
