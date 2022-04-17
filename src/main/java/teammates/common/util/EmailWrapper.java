package teammates.common.util;

/**
 * Represents an email message and its important metadata.
 */
public class EmailWrapper {

    /**
     * Prefix appended to the subject if the email is set as a copy.
     */
    public static final String EMAIL_COPY_SUBJECT_PREFIX = "[Email Copy] ";

    private EmailType type;
    private String senderName;
    private String senderEmail;
    private String replyTo;
    private String recipient;
    private String bcc;
    private String subject;
    private String content;
    private boolean isCopy;

    public EmailType getType() {
        return type;
    }

    public void setType(EmailType type) {
        this.type = type;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets the email subject based on the email type and inserts the specified {@code params}
     * in the indicated places.
     */
    public void setSubjectFromType(Object... params) {
        if (type != null) {
            this.subject = String.format(type.getSubject(), params);
        }
        if (isCopy) {
            this.subject = EMAIL_COPY_SUBJECT_PREFIX + this.subject;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsCopy() {
        return isCopy;
    }

    public void setIsCopy(boolean isCopy) {
        this.isCopy = isCopy;
    }

}
