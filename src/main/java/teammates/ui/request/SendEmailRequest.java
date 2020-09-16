package teammates.ui.request;

import teammates.common.util.EmailWrapper;

/**
 * The request of sending an email.
 */
public class SendEmailRequest extends BasicRequest {
    private final EmailWrapper email;

    public SendEmailRequest(EmailWrapper email) {
        this.email = email;
    }

    public EmailWrapper getEmail() {
        return email;
    }

    @Override
    public void validate() {
        assertTrue(email != null, "Email cannot be null");
        assertTrue(email.getContent() != null, "Email content cannot be null");
        assertTrue(email.getRecipient() != null, "Email recipient's address cannot be null");
        assertTrue(email.getSenderEmail() != null, "Email sender's address cannot be null");
        assertTrue(email.getReplyTo() != null, "Email reply-to address cannot be null");
        assertTrue(email.getSubject() != null, "Email subject cannot be null");
    }

}
