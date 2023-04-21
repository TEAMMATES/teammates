package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.SendLoginEmailResponseData;

/**
 * SUT: {@link SendLoginEmailAction}.
 */
public class SendLoginEmailActionTest extends BaseActionTest<SendLoginEmailAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LOGIN_EMAIL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    protected void testExecute_notEnoughParameters() {
        ______TS("Invalid parameters");
        // no params
        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.USER_EMAIL, "test@example.com");
    }

    @Test
    protected void testExecute_invalidEmail_shouldFail() {
        ______TS("email address is not valid");
        String[] invalidEmailParam = new String[] {
                Const.ParamsNames.USER_EMAIL, "invalid-email-address",
        };

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(invalidEmailParam);
        assertEquals("Invalid email address: invalid-email-address", ihpe.getMessage());
    }

    @Test
    protected void testExecute_validEmail() {
        ______TS("Typical case: valid email address");

        String[] loginParams = new String[] {
                Const.ParamsNames.USER_EMAIL, "test@example.com",
                Const.ParamsNames.CONTINUE_URL, "http://localhost:4200",
        };

        SendLoginEmailAction a = getAction(loginParams);
        JsonResult result = getJsonResult(a);

        SendLoginEmailResponseData output = (SendLoginEmailResponseData) result.getOutput();

        assertEquals("The login link has been sent to the specified email address: test@example.com",
                output.getMessage());
        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(EmailType.LOGIN.getSubject(), emailSent.getSubject());
        assertEquals("test@example.com", emailSent.getRecipient());
    }

    @Override
    @Test
    protected void testExecute() {
        // see individual tests
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }
}
