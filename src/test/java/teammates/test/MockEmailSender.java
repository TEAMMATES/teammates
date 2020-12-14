package teammates.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailSender;

/**
 * Allows mocking of the {@link EmailSender} API used in production.
 *
 * <p>Instead of actually sending the email via the configured email sending service,
 * the API will perform some operations that allow the sent emails to be tracked.
 */
public class MockEmailSender extends EmailSender {

    private List<EmailWrapper> sentEmails = new ArrayList<>();

    @Override
    public EmailSendingStatus sendEmail(EmailWrapper email) {
        sentEmails.add(email);
        return new EmailSendingStatus(HttpStatus.SC_OK, null);
    }

    @Override
    public List<EmailWrapper> getEmailsSent() {
        return sentEmails;
    }

}
