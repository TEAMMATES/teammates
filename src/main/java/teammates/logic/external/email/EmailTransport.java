package teammates.logic.external.email;

import teammates.common.exception.EmailSendingException;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * An email transport interface used by services for delivering emails.
 */
public interface EmailTransport {

    /**
     * Delivers the email packaged as a {@code wrapper}.
     */
    EmailSendingStatus deliver(EmailWrapper wrapper) throws EmailSendingException;

}
