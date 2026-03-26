package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
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
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetEmailTemplatesAction;
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
    void testExecute_resetToDefault_deletesAndReturnsFallback() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, null, null, true);

        UpdateEmailTemplateAction action = getAction(requestBody);
        EmailTemplateData output = (EmailTemplateData) getJsonResult(action).getOutput();

        verify(mockLogic).deleteEmailTemplate(VALID_TEMPLATE_KEY);
        assertEquals(VALID_TEMPLATE_KEY, output.getTemplateKey());
        assertEquals(GetEmailTemplatesAction.DEFAULT_SUBJECTS.get(VALID_TEMPLATE_KEY), output.getSubject());
        assertEquals(GetEmailTemplatesAction.DEFAULT_BODIES.get(VALID_TEMPLATE_KEY), output.getBody());
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

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(requestBody);
        assertEquals("templateKey cannot be null or empty", ex.getMessage());
    }

    @Test
    void testExecute_blankTemplateKey_throwsInvalidHttpRequestBodyException() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                "   ", "Subject", "<p>Body</p>", false);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(requestBody);
        assertEquals("templateKey cannot be null or empty", ex.getMessage());
    }

    @Test
    void testExecute_nullSubject_throwsInvalidHttpRequestBodyException() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, null, "<p>Body</p>", false);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(requestBody);
        assertEquals("subject cannot be null or empty", ex.getMessage());
    }

    @Test
    void testExecute_blankSubject_throwsInvalidHttpRequestBodyException() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, "   ", "<p>Body</p>", false);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(requestBody);
        assertEquals("subject cannot be null or empty", ex.getMessage());
    }

    @Test
    void testExecute_nullBody_throwsInvalidHttpRequestBodyException() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, "Subject", null, false);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(requestBody);
        assertEquals("body cannot be null or empty", ex.getMessage());
    }

    @Test
    void testExecute_blankBody_throwsInvalidHttpRequestBodyException() {
        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, "Subject", "   ", false);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(requestBody);
        assertEquals("body cannot be null or empty", ex.getMessage());
    }

    @Test
    void testExecute_dbThrowsInvalidParametersException_throwsInvalidHttpRequestBodyException()
            throws InvalidParametersException {
        when(mockLogic.upsertEmailTemplate(any(EmailTemplate.class)))
                .thenThrow(new InvalidParametersException("Db Error"));

        EmailTemplateUpdateRequest requestBody = new EmailTemplateUpdateRequest(
                VALID_TEMPLATE_KEY, "Subject", "<p>Body</p>", false);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(requestBody);
        assertEquals("Db Error", ex.getMessage());
    }
}
