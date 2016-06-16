package teammates.logic.core;

import teammates.common.util.EmailWrapper;

/**
 * An email sender interface used by services for sending emails.
 */
public interface EmailSenderService {
    
    /**
     * Parses the {@code wrapper} email object to specific implementations of email object
     * used by the service.
     */
    Object parseToEmail(EmailWrapper wrapper) throws Exception;
    
    /**
     * Sends the email packaged as a {@code wrapper}.
     */
    void sendEmail(EmailWrapper wrapper) throws Exception;
    
}
