package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.ui.output.EmailTemplateData;
import teammates.ui.webapi.ConfigurableEmailTemplate;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetEmailTemplateAction;
import teammates.ui.webapi.InvalidHttpParameterException;

/**
 * SUT: {@link GetEmailTemplateAction}.
 */
public class GetEmailTemplateActionTest extends BaseActionTest<GetEmailTemplateAction> {

    private static final String VALID_TEMPLATE_KEY = "NEW_INSTRUCTOR_ACCOUNT_WELCOME";

    @Override
    String getActionUri() {
        return Const.ResourceURIs.EMAIL_TEMPLATE;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        loginAsAdmin();
    }

    @Test
    void testAccessControl_onlyAdminsCanAccess() {
        verifyOnlyAdminsCanAccess(Const.ParamsNames.TEMPLATE_KEY, VALID_TEMPLATE_KEY);
    }

    @Test
    void testExecute_customTemplateExists_returnsCustomTemplate() {
        EmailTemplate customTemplate = new EmailTemplate(
                VALID_TEMPLATE_KEY, "Custom Subject", "<p>Custom body</p>");

        when(mockLogic.getEmailTemplate(VALID_TEMPLATE_KEY)).thenReturn(customTemplate);

        GetEmailTemplateAction action = getAction(Const.ParamsNames.TEMPLATE_KEY, VALID_TEMPLATE_KEY);
        EmailTemplateData output = (EmailTemplateData) getJsonResult(action).getOutput();

        assertEquals(VALID_TEMPLATE_KEY, output.getTemplateKey());
        assertEquals("Custom Subject", output.getSubject());
        assertEquals("<p>Custom body</p>", output.getBody());
        assertTrue(output.getIsCustomized());
    }

    @Test
    void testExecute_noCustomTemplate_returnsFallback() {
        when(mockLogic.getEmailTemplate(VALID_TEMPLATE_KEY)).thenReturn(null);

        GetEmailTemplateAction action = getAction(Const.ParamsNames.TEMPLATE_KEY, VALID_TEMPLATE_KEY);
        EmailTemplateData output = (EmailTemplateData) getJsonResult(action).getOutput();

        assertEquals(VALID_TEMPLATE_KEY, output.getTemplateKey());
        assertEquals(ConfigurableEmailTemplate.NEW_INSTRUCTOR_ACCOUNT_WELCOME.getDefaultSubject(),
                output.getSubject());
        assertEquals(ConfigurableEmailTemplate.NEW_INSTRUCTOR_ACCOUNT_WELCOME.getDefaultBody(),
                output.getBody());
        assertFalse(output.getIsCustomized());
    }

    @Test
    void testExecute_unknownTemplateKey_throwsEntityNotFoundException() {
        EntityNotFoundException enfe = verifyEntityNotFound(
                Const.ParamsNames.TEMPLATE_KEY, "UNKNOWN_KEY");

        assertEquals("Email template with key 'UNKNOWN_KEY' does not exist.", enfe.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingTemplateKey_throwsInvalidHttpParameterException() {
        GetEmailTemplateAction action = getAction(new String[] { Const.ParamsNames.TEMPLATE_KEY, null });
        assertThrows(InvalidHttpParameterException.class, action::execute);
    }
}
