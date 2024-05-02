package teammates.common.datatransfer.logs;

import jakarta.annotation.Nullable;

import teammates.common.util.EmailType;

/**
 * Contains specific structure and processing logic for email sent log.
 */
public class EmailSentLogDetails extends LogDetails {

    @Nullable
    private String emailRecipient;
    @Nullable
    private String emailSubject;
    @Nullable
    private String emailContent;
    private EmailType emailType;
    private int emailStatus;
    private String emailStatusMessage;

    public EmailSentLogDetails() {
        super(LogEvent.EMAIL_SENT);
    }

    public String getEmailRecipient() {
        return emailRecipient;
    }

    public void setEmailRecipient(String emailRecipient) {
        this.emailRecipient = emailRecipient;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public int getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(int emailStatus) {
        this.emailStatus = emailStatus;
    }

    public String getEmailStatusMessage() {
        return emailStatusMessage;
    }

    public void setEmailStatusMessage(String emailStatusMessage) {
        this.emailStatusMessage = emailStatusMessage;
    }

    @Override
    public void hideSensitiveInformation() {
        emailRecipient = null;
        emailSubject = null;
        emailContent = null;
    }

}
