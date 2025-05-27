package teammates.logic.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * Allows mocking of the {@link EmailSender} API used in production.
 *
 * <p>Instead of actually sending the email via the configured email sending service,
 * the API will perform some operations that allow the sent emails to be tracked.
 */
public class MockEmailSender extends EmailSender {

    private List<EmailWrapper> sentEmails = new ArrayList<>();
    private boolean shouldFail;

    @Override
    public EmailSendingStatus sendEmail(EmailWrapper email) {
        if (shouldFail) {
            return new EmailSendingStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
        }

        sentEmails.add(email);
        return new EmailSendingStatus(HttpStatus.SC_OK, null);
    }

    /**
     * Sets whether email sending should fail.
     *
     * @param shouldFail if true, email sending will fail.
     */
    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    /**
     * Gets the emails sent.
     */
    public List<EmailWrapper> getEmailsSent() {
        return sentEmails;
    }

    /**
     * Clears the list of emails sent.
     */
    public void clearEmails() {
        sentEmails.clear();
    }

}
