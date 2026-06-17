package teammates.logic.external.email;

import org.apache.http.HttpStatus;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * Email transport which will not deliver any email.
 */
public class EmptyEmailTransport implements EmailTransport {

    EmailWrapper parseToEmail(EmailWrapper wrapper) {
        return wrapper;
    }

    @Override
    public EmailSendingStatus deliver(EmailWrapper wrapper) {
        return new EmailSendingStatus(HttpStatus.SC_OK, null);
    }

}
