package teammates.ui.output;

import teammates.common.util.EmailWrapper;

/**
 * The output format for email request.
 */
public class EmailData extends ApiOutput {
    private final String recipient;
    private final String subject;
    private final String content;

    public EmailData(EmailWrapper email) {
        this.recipient = email.getRecipient();
        this.subject = email.getSubject();
        this.content = email.getContent();
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

}
