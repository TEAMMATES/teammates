package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.SendLoginEmailResponseData;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SendLoginEmailAction;

/**
 * SUT: {@link SendLoginEmailAction}.
 */
public class SendLoginEmailActionTest extends BaseActionTest<SendLoginEmailAction> {
    private static final String GOOGLE_ID = "user-googleId";
    private static final String USER_EMAIL = "test@example.com";
    private static final String CONTINUE_URL = "http://localhost:4200";
    private static final String USER_CAPTCHA_RESPONSE = "user-captcha-response";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LOGIN_EMAIL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    void testExecute_typicalCase_success() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(true);
        when(mockRecaptchaVerifier.isVerificationSuccessful(USER_CAPTCHA_RESPONSE)).thenReturn(true);
        when(mockAuthProxy.generateLoginLink(USER_EMAIL, CONTINUE_URL)).thenReturn("http://localhost:4201");

        String[] loginParams = new String[] {
                Const.ParamsNames.USER_EMAIL, USER_EMAIL,
                Const.ParamsNames.CONTINUE_URL, CONTINUE_URL,
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, USER_CAPTCHA_RESPONSE,
        };

        EmailWrapper stubEmailWrapper = new EmailWrapper();
        stubEmailWrapper.setBcc(Config.SUPPORT_EMAIL);
        stubEmailWrapper.setRecipient(USER_EMAIL);
        stubEmailWrapper.setType(EmailType.LOGIN);
        stubEmailWrapper.setSubjectFromType();
        when(mockEmailGenerator.generateLoginEmail(USER_EMAIL, "http://localhost:4201")).thenReturn(stubEmailWrapper);

        SendLoginEmailAction a = getAction(loginParams);
        JsonResult result = getJsonResult(a);
        SendLoginEmailResponseData output = (SendLoginEmailResponseData) result.getOutput();

        assertEquals("The login link has been sent to the specified email address: test@example.com",
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(EmailType.LOGIN.getSubject(), emailSent.getSubject());
        assertEquals(USER_EMAIL, emailSent.getRecipient());
    }

    @Test
    void testExecute_loginEmailDisabled_throwsInvalidOperationException() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(false);

        String[] loginParams = new String[] {
                Const.ParamsNames.USER_EMAIL, USER_EMAIL,
                Const.ParamsNames.CONTINUE_URL, CONTINUE_URL,
        };

        InvalidOperationException ioe = verifyInvalidOperation(loginParams);
        assertEquals("Login using email link is not supported", ioe.getMessage());
    }

    @Test
    void testExecute_emptyParams_throwsInvalidHttpParameterException() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(true);
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidUserEmailParam_throwsInvalidHttpParameterException() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(true);

        ______TS("Missing userEmail parameter");
        verifyHttpParameterFailure(Const.ParamsNames.CONTINUE_URL, CONTINUE_URL);

        ______TS("Null userEmail parameter");
        String[] nullEmailParams = new String[] {
                Const.ParamsNames.USER_EMAIL, null,
                Const.ParamsNames.CONTINUE_URL, CONTINUE_URL,
        };

        verifyHttpParameterFailure(nullEmailParams);

        ______TS("Invalid userEmail");
        String[] invalidEmailParams = new String[] {
                Const.ParamsNames.USER_EMAIL, "invalid-email",
                Const.ParamsNames.CONTINUE_URL, CONTINUE_URL,
        };

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(invalidEmailParams);
        assertEquals("Invalid email address: invalid-email", ihpe.getMessage());
    }

    @Test
    void testExecute_recaptchaVerificationFailed_failure() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(true);
        when(mockRecaptchaVerifier.isVerificationSuccessful(USER_CAPTCHA_RESPONSE)).thenReturn(false);

        String[] loginParams = new String[] {
                Const.ParamsNames.USER_EMAIL, USER_EMAIL,
                Const.ParamsNames.CONTINUE_URL, CONTINUE_URL,
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, USER_CAPTCHA_RESPONSE,
        };

        SendLoginEmailAction a = getAction(loginParams);
        JsonResult result = getJsonResult(a);
        SendLoginEmailResponseData output = (SendLoginEmailResponseData) result.getOutput();

        assertEquals("ReCAPTCHA verification failed. Please try again.", output.getMessage());
        verifyNumberOfEmailsSent(0);
    }

    @Test
    void testExecute_invalidContinueUrlParam_throwsInvalidHttpParameterException() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(true);
        when(mockRecaptchaVerifier.isVerificationSuccessful(USER_CAPTCHA_RESPONSE)).thenReturn(true);

        ______TS("Missing continueUrl parameter");
        String[] missingContinueUrlParams = new String[] {
                Const.ParamsNames.USER_EMAIL, USER_EMAIL,
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, USER_CAPTCHA_RESPONSE,
        };
        verifyHttpParameterFailure(missingContinueUrlParams);

        ______TS("Null continueUrl parameter");
        String[] nullContinueUrlParams = new String[] {
                Const.ParamsNames.USER_EMAIL, USER_EMAIL,
                Const.ParamsNames.CONTINUE_URL, null,
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, USER_CAPTCHA_RESPONSE,
        };
        verifyHttpParameterFailure(nullContinueUrlParams);
    }

    @Test
    void testExecute_failedToGenerateLoginLink_failure() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(true);
        when(mockRecaptchaVerifier.isVerificationSuccessful(USER_CAPTCHA_RESPONSE)).thenReturn(true);
        when(mockAuthProxy.generateLoginLink(USER_EMAIL, CONTINUE_URL)).thenReturn(null);

        String[] loginParams = new String[] {
                Const.ParamsNames.USER_EMAIL, USER_EMAIL,
                Const.ParamsNames.CONTINUE_URL, CONTINUE_URL,
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, USER_CAPTCHA_RESPONSE,
        };

        SendLoginEmailAction a = getAction(loginParams);
        JsonResult result = getJsonResult(a);
        SendLoginEmailResponseData output = (SendLoginEmailResponseData) result.getOutput();

        assertEquals("An error occurred. The email could not be generated.", output.getMessage());
        verifyNumberOfEmailsSent(0);
    }

    @Test
    void testExecute_failedToSendEmail_failure() {
        when(mockAuthProxy.isLoginEmailEnabled()).thenReturn(true);
        when(mockRecaptchaVerifier.isVerificationSuccessful(USER_CAPTCHA_RESPONSE)).thenReturn(true);
        when(mockAuthProxy.generateLoginLink(USER_EMAIL, CONTINUE_URL)).thenReturn("http://localhost:4201");
        mockEmailSender.setShouldFail(true);

        String[] loginParams = new String[] {
                Const.ParamsNames.USER_EMAIL, USER_EMAIL,
                Const.ParamsNames.CONTINUE_URL, CONTINUE_URL,
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, USER_CAPTCHA_RESPONSE,
        };

        SendLoginEmailAction a = getAction(loginParams);
        JsonResult result = getJsonResult(a);
        SendLoginEmailResponseData output = (SendLoginEmailResponseData) result.getOutput();

        assertEquals("An error occurred. The email could not be sent.", output.getMessage());
        verifyNumberOfEmailsSent(0);

        mockEmailSender.setShouldFail(false);
    }

    @Test
    void testAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_maintainer_canAccess() {
        loginAsMaintainer();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_instructor_canAccess() {
        loginAsInstructor(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_student_canAccess() {
        loginAsStudent(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_unregistered_canAccess() {
        loginAsUnregistered(GOOGLE_ID);
        verifyCanAccess();
    }

    @Test
    void testAccessControl_loggedOut_canAccess() {
        logoutUser();
        verifyCanAccess();
    }
}
