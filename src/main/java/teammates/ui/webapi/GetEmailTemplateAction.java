package teammates.ui.webapi;

import teammates.common.util.Const;
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

    @Override
    public JsonResult execute() {
        String templateKey = getNonNullRequestParamValue(Const.ParamsNames.TEMPLATE_KEY);

        ConfigurableEmailTemplate configTemplate;
        try {
            configTemplate = ConfigurableEmailTemplate.valueOf(templateKey);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Email template with key '" + templateKey + "' does not exist.");
        }

        EmailTemplate emailTemplate = sqlLogic.getEmailTemplate(templateKey);

        if (emailTemplate != null) {
            return new JsonResult(new EmailTemplateData(emailTemplate));
        }

        // Fall back to the static file so the frontend can show the current default.
        return new JsonResult(new EmailTemplateData(templateKey,
                configTemplate.getDefaultSubject(), configTemplate.getDefaultBody()));
    }
}
