package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.SessionLinksRecoveryResponseData;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SessionLinksRecoveryAction;

/**
 * SUT: {@link SessionLinksRecoveryAction}.
 */
public class SessionLinksRecoveryActionTest extends BaseActionTest<SessionLinksRecoveryAction> {
    private Student stubStudent;
    private EmailWrapper stubEmailWrapper;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LINKS_RECOVERY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        stubStudent = getTypicalStudent();
        stubEmailWrapper = new EmailWrapper();
        stubEmailWrapper.setRecipient(stubStudent.getEmail());
        stubEmailWrapper.setSubject("TEAMMATES: Recovery Email");
        reset(mockLogic, mockSqlEmailGenerator);
    }

    @Test
    void testExecute_invalidEmailParam_throwsInvalidHttpParameterException() {
        String[] params1 = {};
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.STUDENT_EMAIL, null,
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.STUDENT_EMAIL, "invalid-email-address",
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params3);
        assertEquals("Invalid email address: invalid-email-address", ihpe.getMessage());
    }

    @Test
    void testExecute_nonExistentEmail_success() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent@email.com",
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "correct-captcha-response",
        };

        stubEmailWrapper.setRecipient("non-existent@email.com");
        when(mockRecaptchaVerifier.isVerificationSuccessful(params[3])).thenReturn(true);
        when(mockDatastoreLogic.getAllStudentsForEmail("non-existent@email.com")).thenReturn(List.of());
        when(mockLogic.getAllStudentsForEmail("non-existent@email.com")).thenReturn(List.of());
        when(mockSqlEmailGenerator.generateSessionLinksRecoveryEmailForStudent(eq("non-existent@email.com")))
                .thenReturn(stubEmailWrapper);
        mockEmailSender.setShouldFail(false);

        SessionLinksRecoveryAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();
        assertEquals("The recovery links for your feedback sessions have been sent to the "
                + "specified email address: non-existent@email.com", output.getMessage());
        verifyNumberOfEmailsSent(1);
        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals("TEAMMATES: Recovery Email", emailSent.getSubject());
        assertEquals("non-existent@email.com", emailSent.getRecipient());
    }

    @Test
    void testExecute_existingStudent_success() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "correct-captcha-response",
        };

        when(mockRecaptchaVerifier.isVerificationSuccessful(params[3])).thenReturn(true);
        when(mockSqlEmailGenerator.generateSessionLinksRecoveryEmailForStudent(eq(stubStudent.getEmail())))
                .thenReturn(stubEmailWrapper);
        mockEmailSender.setShouldFail(false);

        SessionLinksRecoveryAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();
        assertTrue(output.isEmailSent());
        assertEquals("The recovery links for your feedback sessions have been sent to the "
                + "specified email address: " + stubStudent.getEmail(), output.getMessage());

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals("TEAMMATES: Recovery Email", emailSent.getSubject());
        assertEquals(stubStudent.getEmail(), emailSent.getRecipient());
    }

    @Test
    void testExecute_captchaVerificationFailed_returnsFalseResponse() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "incorrect-captcha-response",
        };

        when(mockRecaptchaVerifier.isVerificationSuccessful(params[3])).thenReturn(false);

        SessionLinksRecoveryAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();
        assertFalse(output.isEmailSent());
        assertEquals("Something went wrong with the reCAPTCHA verification. Please try again.",
                output.getMessage());

        verifyNoEmailsSent();
    }

    @Test
    void testExecute_emailSendingFails_returnsFalseResponse() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "correct-captcha-response",
        };

        when(mockRecaptchaVerifier.isVerificationSuccessful(params[3])).thenReturn(true);
        when(mockSqlEmailGenerator.generateSessionLinksRecoveryEmailForStudent(eq(stubStudent.getEmail())))
                .thenReturn(stubEmailWrapper);
        mockEmailSender.setShouldFail(true);

        SessionLinksRecoveryAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        SessionLinksRecoveryResponseData output = (SessionLinksRecoveryResponseData) result.getOutput();
        assertFalse(output.isEmailSent());
        assertEquals("An error occurred. The email could not be sent.", output.getMessage());
    }

    @Test
    protected void testAccessControl() {
        verifyCanAccess();
    }
}
