package teammates.logic.core;

import java.util.logging.Logger;

import teammates.common.util.EmailWrapper;
import teammates.common.util.Utils;

/**
 * An email sender interface used by services for sending emails.
 */
public interface EmailSenderService {
    
    int SUCCESS_CODE = 200;
    
    Logger log = Utils.getLogger();
    
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
