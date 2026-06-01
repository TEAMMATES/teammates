package teammates.ui.request;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.common.util.EmailWrapper;

/**
 * The request of sending an email.
 */
public class SendEmailRequest extends BasicRequest {
    private final EmailWrapper email;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SendEmailRequest(EmailWrapper email) {
        this.email = email;
    }

    public EmailWrapper getEmail() {
        return email;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(email != null, "Email cannot be null");
        validateTrue(email.getContent() != null, "Email content cannot be null");
        validateTrue(email.getRecipient() != null, "Email recipient's address cannot be null");
        validateTrue(email.getSenderEmail() != null, "Email sender's address cannot be null");
        validateTrue(email.getReplyTo() != null, "Email reply-to address cannot be null");
        validateTrue(email.getSubject() != null, "Email subject cannot be null");
    }

}
