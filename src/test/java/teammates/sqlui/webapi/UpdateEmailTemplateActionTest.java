package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.ui.output.EmailTemplateData;
import teammates.ui.request.EmailTemplateUpdateRequest;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.UpdateEmailTemplateAction;

/**
 * SUT: {@link UpdateEmailTemplateAction}.
 */
public class UpdateEmailTemplateActionTest extends BaseActionTest<UpdateEmailTemplateAction> {

    private static final String VALID_TEMPLATE_KEY = "NEW_INSTRUCTOR_ACCOUNT_WELCOME";

    @Override
    String getActionUri() {
        return Const.ResourceURIs.EMAIL_TEMPLATE;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        reset(mockLogic);
        loginAsAdmin();
    }

    @Test
    void testAccessControl_onlyAdminsCanAccess() {
        verifyOnlyAdminsCanAccess();
    }

    @Test
    void testExecute_saveValidTemplate_success() throws InvalidParametersException {
        EmailTemplate savedTemplate = new EmailTemplate(
                VALID_TEMPLATE_KEY, "New Subject", "<p>New body</p>");
        when(mockLogic.upsertEmailTemplate(any(EmailTemplate.class))).thenReturn(savedTemplate);

        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, "New Subject", "<p>New body</p>", false);

        UpdateEmailTemplateAction action = getAction(requestBody);
        EmailTemplateData output = (EmailTemplateData) getJsonResult(action).getOutput();

        assertEquals(VALID_TEMPLATE_KEY, output.getTemplateKey());
        assertEquals("New Subject", output.getSubject());
        assertEquals("<p>New body</p>", output.getBody());
        assertTrue(output.getIsCustomized());
    }

    @Test
    void testExecute_resetToDefault_existingTemplate_deletesAndReturnsFallback() {
        EmailTemplate existingTemplate = new EmailTemplate(
                VALID_TEMPLATE_KEY, "Old Subject", "<p>Old body</p>");
        when(mockLogic.getEmailTemplate(VALID_TEMPLATE_KEY)).thenReturn(existingTemplate);

        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, null, null, true);

        UpdateEmailTemplateAction action = getAction(requestBody);
        EmailTemplateData output = (EmailTemplateData) getJsonResult(action).getOutput();

        verify(mockLogic).deleteEmailTemplate(existingTemplate);
        assertEquals(VALID_TEMPLATE_KEY, output.getTemplateKey());
        assertNotNull(output.getSubject());
        assertNotNull(output.getBody());
        assertFalse(output.getIsCustomized());
    }

    @Test
    void testExecute_resetToDefault_noExistingTemplate_returnsFallback() {
        when(mockLogic.getEmailTemplate(VALID_TEMPLATE_KEY)).thenReturn(null);

        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, null, null, true);

        UpdateEmailTemplateAction action = getAction(requestBody);
        EmailTemplateData output = (EmailTemplateData) getJsonResult(action).getOutput();

        verify(mockLogic, never()).deleteEmailTemplate(any());
        assertEquals(VALID_TEMPLATE_KEY, output.getTemplateKey());
        assertNotNull(output.getSubject());
        assertNotNull(output.getBody());
        assertFalse(output.getIsCustomized());
    }

    @Test
    void testExecute_unknownTemplateKey_throwsEntityNotFoundException() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                "UNKNOWN_KEY", "Subject", "<p>Body</p>", false);

        EntityNotFoundException enfe = verifyEntityNotFound(requestBody);
        assertEquals("Email template with key 'UNKNOWN_KEY' does not exist.", enfe.getMessage());
    }

    @Test
    void testExecute_nullTemplateKey_throwsInvalidHttpRequestBodyException() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                null, "Subject", "<p>Body</p>", false);

        verifyHttpRequestBodyFailure(requestBody);
    }
}
