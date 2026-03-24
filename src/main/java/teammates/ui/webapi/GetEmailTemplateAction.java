package teammates.ui.webapi;

import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.Templates;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.ui.output.EmailTemplateData;

/**
 * Gets an email template by its key.
 *
 * <p>If a custom template has been saved to the database, that is returned.
 * Otherwise the response contains the static file content so the frontend
 * can pre-populate the editor with the current default.
 */
public class GetEmailTemplateAction extends AdminOnlyAction {

    /**
     * Fallback subjects used when the template has not been customised.
     * These mirror the subjects hardcoded in {@code SqlEmailGenerator}.
     */
    private static final Map<String, String> DEFAULT_SUBJECTS = Map.of(
            "NEW_INSTRUCTOR_ACCOUNT_WELCOME", "Welcome to TEAMMATES!"
    );

    /**
     * Fallback bodies used when the template has not been customised.
     * Each value is the content of the corresponding static HTML resource file.
     */
    private static final Map<String, String> DEFAULT_BODIES = Map.of(
            "NEW_INSTRUCTOR_ACCOUNT_WELCOME", Templates.EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME
    );

    @Override
    public JsonResult execute() {
        String templateKey = getNonNullRequestParamValue(Const.ParamsNames.TEMPLATE_KEY);

        if (!GetEmailTemplatesAction.CONFIGURABLE_TEMPLATE_KEYS.contains(templateKey)) {
            throw new EntityNotFoundException("Email template with key '" + templateKey + "' does not exist.");
        }

        EmailTemplate emailTemplate = sqlLogic.getEmailTemplate(templateKey);

        if (emailTemplate != null) {
            return new JsonResult(new EmailTemplateData(emailTemplate));
        }

        // Fall back to the static file so the frontend can show the current default.
        String defaultSubject = DEFAULT_SUBJECTS.get(templateKey);
        String defaultBody = DEFAULT_BODIES.get(templateKey);
        return new JsonResult(new EmailTemplateData(templateKey, defaultSubject, defaultBody));
    }
}
