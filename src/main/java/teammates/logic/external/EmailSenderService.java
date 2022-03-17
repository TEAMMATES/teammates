package teammates.logic.external;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * An email sender interface used by services for sending emails.
 */
public interface EmailSenderService {

    /**
     * Parses the {@code wrapper} email object to specific implementations of email object
     * used by the service.
     */
    Object parseToEmail(EmailWrapper wrapper);

    /**
     * Sends the email packaged as a {@code wrapper}.
     */
    EmailSendingStatus sendEmail(EmailWrapper wrapper) throws EmailSendingException;

}
