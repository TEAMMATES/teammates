package teammates.ui.webapi;

import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.ui.output.EmailTemplateData;
import teammates.ui.request.EmailTemplateUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates (or resets) a configurable email template.
 *
 * <p>When {@code resetToDefault} is {@code true} in the request body,
 * the custom DB record is deleted and the static file fallback resumes.
 * Otherwise the template is created or updated in the database.
 */
public class UpdateEmailTemplateAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        EmailTemplateUpdateRequest updateRequest = getAndValidateRequestBody(EmailTemplateUpdateRequest.class);

        String templateKey = updateRequest.getTemplateKey();

        if (!GetEmailTemplatesAction.CONFIGURABLE_TEMPLATE_KEYS.contains(templateKey)) {
            throw new EntityNotFoundException("Email template with key '" + templateKey + "' does not exist.");
        }

        if (updateRequest.isResetToDefault()) {
            sqlLogic.deleteEmailTemplate(templateKey);
            String defaultSubject = GetEmailTemplatesAction.DEFAULT_SUBJECTS.get(templateKey);
            String defaultBody = GetEmailTemplatesAction.DEFAULT_BODIES.get(templateKey);
            return new JsonResult(new EmailTemplateData(templateKey, defaultSubject, defaultBody));
        }

        EmailTemplate emailTemplate = new EmailTemplate(
                templateKey,
                updateRequest.getSubject(),
                updateRequest.getBody());

        try {
            emailTemplate = sqlLogic.upsertEmailTemplate(emailTemplate);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        return new JsonResult(new EmailTemplateData(emailTemplate));
    }
}
