package teammates.logic.external;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * An email sender interface used by services for sending emails.
 */
public interface EmailSenderService {

    /**
     * Sends the email packaged as a {@code wrapper}.
     */
    EmailSendingStatus sendEmail(EmailWrapper wrapper) throws EmailSendingException;

}
