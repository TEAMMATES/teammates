package teammates.ui.request;

/**
 * The request body for creating or updating an email template.
 */
public class EmailTemplateUpdateRequest extends BasicRequest {
    private String templateKey;
    private String subject;
    private String body;
    private boolean resetToDefault;

    public EmailTemplateUpdateRequest(String templateKey, String subject, String body, boolean resetToDefault) {
        this.templateKey = templateKey;
        this.subject = subject;
        this.body = body;
        this.resetToDefault = resetToDefault;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(templateKey != null && !templateKey.isBlank(), "templateKey cannot be null or empty");
        if (!resetToDefault) {
            assertTrue(subject != null && !subject.isBlank(), "subject cannot be null or empty");
            assertTrue(body != null && !body.isBlank(), "body cannot be null or empty");
        }
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

    public boolean isResetToDefault() {
        return this.resetToDefault;
    }
}
