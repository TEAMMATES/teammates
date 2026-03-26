package teammates.ui.output;

import jakarta.annotation.Nullable;

import teammates.storage.sqlentity.EmailTemplate;

/**
 * Output format for a single email template.
 */
public class EmailTemplateData extends ApiOutput {
    private String templateKey;
    private String subject;
    private String body;
    private boolean isCustomized;

    @Nullable
    private Long updatedAt;

    /**
     * Constructs output from a DB-backed custom template.
     */
    public EmailTemplateData(EmailTemplate emailTemplate) {
        this.templateKey = emailTemplate.getTemplateKey();
        this.subject = emailTemplate.getSubject();
        this.body = emailTemplate.getBody();
        this.isCustomized = true;
        this.updatedAt = emailTemplate.getUpdatedAt() != null
                ? emailTemplate.getUpdatedAt().toEpochMilli()
                : null;
    }

    /**
     * Constructs output for a static fallback (no custom template in DB).
     */
    public EmailTemplateData(String templateKey, String defaultSubject, String defaultBody) {
        this.templateKey = templateKey;
        this.subject = defaultSubject;
        this.body = defaultBody;
        this.isCustomized = false;
        this.updatedAt = null;
    }

    public String getTemplateKey() {
        return this.templateKey;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBody() {
        return this.body;
    }

    public boolean getIsCustomized() {
        return this.isCustomized;
    }

    @Nullable
    public Long getUpdatedAt() {
        return this.updatedAt;
    }
}
