package teammates.sqllogic.core;

import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.EmailTemplatesDb;
import teammates.storage.sqlentity.EmailTemplate;

/**
 * Handles the logic related to email templates.
 */
public final class EmailTemplatesLogic {

    private static final EmailTemplatesLogic instance = new EmailTemplatesLogic();

    private EmailTemplatesDb emailTemplatesDb;

    private EmailTemplatesLogic() {
        // prevent initialization
    }

    public static EmailTemplatesLogic inst() {
        return instance;
    }

    /**
     * Initialise dependencies for {@code EmailTemplatesLogic} object.
     */
    public void initLogicDependencies(EmailTemplatesDb emailTemplatesDb) {
        this.emailTemplatesDb = emailTemplatesDb;
    }

    /**
     * Gets the email template associated with {@code templateKey},
     * or {@code null} if no custom template has been saved for that key.
     */
    public EmailTemplate getEmailTemplate(String templateKey) {
        assert templateKey != null;
        return emailTemplatesDb.getEmailTemplate(templateKey);
    }

    /**
     * Creates or updates the email template for the given key.
     *
     * @return the persisted email template.
     * @throws InvalidParametersException if the template is not valid.
     */
    public EmailTemplate upsertEmailTemplate(EmailTemplate emailTemplate) throws InvalidParametersException {
        assert emailTemplate != null;
        return emailTemplatesDb.upsertEmailTemplate(emailTemplate);
    }

    /**
     * Deletes the custom email template for {@code templateKey}, reverting to the
     * static file fallback. Does nothing if no such template exists.
     */
    public void deleteEmailTemplate(String templateKey) {
        assert templateKey != null;
        emailTemplatesDb.deleteEmailTemplate(templateKey);
    }

}
