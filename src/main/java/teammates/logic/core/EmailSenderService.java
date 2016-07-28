package teammates.logic.core;

import java.util.logging.Logger;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Utils;

/**
 * An email sender interface used by services for sending emails.
 */
public abstract class EmailSenderService {
    
    protected static final int SUCCESS_CODE = 200;
    
    protected static final Logger log = Utils.getLogger();
    
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
    
}
