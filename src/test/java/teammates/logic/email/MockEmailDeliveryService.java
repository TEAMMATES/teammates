package teammates.logic.email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.logic.external.email.EmptyEmailTransport;

/**
 * Allows mocking of the {@link EmailDeliveryService} used in production.
 *
 * <p>Instead of delivering emails via an external transport, emails are captured
 * in memory for test assertions.
 */
public class MockEmailDeliveryService extends EmailDeliveryService {

    private final List<EmailWrapper> emailsSent = new ArrayList<>();

    public MockEmailDeliveryService() {
        super(new EmptyEmailTransport());
    }

    @Override
    public EmailSendingStatus deliver(EmailWrapper message) {
        emailsSent.add(message);
        return new EmailSendingStatus(HttpStatus.SC_OK, null);
    }

    public List<EmailWrapper> getEmailsSent() {
        return Collections.unmodifiableList(emailsSent);
    }

    /**
     * Clears the list of emails sent.
     */
    public void clearEmails() {
        emailsSent.clear();
    }

}
