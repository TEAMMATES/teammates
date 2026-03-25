package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlapi.EmailTemplatesDb;
import teammates.storage.sqlentity.EmailTemplate;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link EmailTemplatesLogic}.
 */
public class EmailTemplatesLogicTest extends BaseTestCase {

    private EmailTemplatesLogic emailTemplatesLogic = EmailTemplatesLogic.inst();

    private EmailTemplatesDb emailTemplatesDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        emailTemplatesDb = mock(EmailTemplatesDb.class);
        emailTemplatesLogic.initLogicDependencies(emailTemplatesDb);
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void tearDownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testGetEmailTemplate_templateExists_returnsTemplate() {
        String templateKey = "NEW_INSTRUCTOR_ACCOUNT_WELCOME";
        EmailTemplate expected = new EmailTemplate(templateKey, "Welcome Subject", "<p>Welcome body</p>");

        when(emailTemplatesDb.getEmailTemplate(templateKey)).thenReturn(expected);

        EmailTemplate result = emailTemplatesLogic.getEmailTemplate(templateKey);

        verify(emailTemplatesDb, times(1)).getEmailTemplate(templateKey);
        assertEquals(expected, result);
    }

    @Test
    public void testGetEmailTemplate_templateDoesNotExist_returnsNull() {
        String templateKey = "NEW_INSTRUCTOR_ACCOUNT_WELCOME";

        when(emailTemplatesDb.getEmailTemplate(templateKey)).thenReturn(null);

        EmailTemplate result = emailTemplatesLogic.getEmailTemplate(templateKey);

        verify(emailTemplatesDb, times(1)).getEmailTemplate(templateKey);
        assertNull(result);
    }

    @Test
    public void testUpsertEmailTemplate_validTemplate_returnsPersistedTemplate()
            throws InvalidParametersException {
        EmailTemplate emailTemplate = new EmailTemplate(
                "NEW_INSTRUCTOR_ACCOUNT_WELCOME", "Welcome Subject", "<p>Welcome body</p>");

        when(emailTemplatesDb.upsertEmailTemplate(emailTemplate)).thenReturn(emailTemplate);

        EmailTemplate result = emailTemplatesLogic.upsertEmailTemplate(emailTemplate);

        verify(emailTemplatesDb, times(1)).upsertEmailTemplate(emailTemplate);
        assertEquals(emailTemplate, result);
    }

    @Test
    public void testUpsertEmailTemplate_invalidTemplate_throwsInvalidParametersException()
            throws InvalidParametersException {
        EmailTemplate invalidTemplate = new EmailTemplate("NEW_INSTRUCTOR_ACCOUNT_WELCOME", "Subject", "<p>Body</p>");

        when(emailTemplatesDb.upsertEmailTemplate(invalidTemplate))
                .thenThrow(new InvalidParametersException("Email template body cannot be empty."));

        assertThrows(InvalidParametersException.class,
                () -> emailTemplatesLogic.upsertEmailTemplate(invalidTemplate));
        verify(emailTemplatesDb, times(1)).upsertEmailTemplate(invalidTemplate);
    }

    @Test
    public void testDeleteEmailTemplate_templateExists_deletesTemplate() {
        EmailTemplate emailTemplate = new EmailTemplate(
                "NEW_INSTRUCTOR_ACCOUNT_WELCOME", "Welcome Subject", "<p>Welcome body</p>");

        emailTemplatesLogic.deleteEmailTemplate(emailTemplate);

        verify(emailTemplatesDb, times(1)).deleteEmailTemplate(emailTemplate);
    }

    @Test
    public void testDeleteEmailTemplate_nullTemplate_doesNothing() {
        emailTemplatesLogic.deleteEmailTemplate(null);

        verify(emailTemplatesDb, times(1)).deleteEmailTemplate(null);
    }
}
