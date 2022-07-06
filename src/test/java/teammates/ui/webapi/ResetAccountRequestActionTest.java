package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.JoinLinkData;

/**
 * SUT: {@link ResetAccountRequestAction}.
 */
public class ResetAccountRequestActionTest extends BaseActionTest<ResetAccountRequestAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST_RESET;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        AccountRequestAttributes accountRequest = typicalBundle.accountRequests.get("instructor1OfCourse1");
        AccountRequestAttributes unregisteredAccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        loginAsAdmin();

        ______TS("Failure case: not enough parameters");

        verifyHttpParameterFailure();

        String[] params = {
                // Const.ParamsNames.INSTRUCTOR_EMAIL,
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, accountRequest.getInstitute(),
        };

        verifyHttpParameterFailure(params);

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                // Const.ParamNames.INSTRUCTOR_INSTITUTION,
        };

        verifyHttpParameterFailure(params);

        ______TS("Failure case: account request not found");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "not-found@gmail.tmt",
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, "not-found-institute",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Account request for instructor with email: not-found@gmail.tmt and institute: "
                + "not-found-institute does not exist.", enfe.getMessage());

        ______TS("Failure case: instructor is unregistered");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, unregisteredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, unregisteredAccountRequest.getInstitute(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Unable to reset account request as instructor is still unregistered.", ioe.getMessage());

        ______TS("typical success case");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTE, accountRequest.getInstitute(),
        };

        ResetAccountRequestAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        accountRequest = logic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        JoinLinkData response = (JoinLinkData) r.getOutput();
        assertEquals(accountRequest.getRegistrationUrl(), response.getJoinLink());

        AccountRequestAttributes updatedAccountRequest =
                logic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertNull(updatedAccountRequest.getRegisteredAt());

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), accountRequest.getName()),
                emailSent.getSubject());
        assertEquals(accountRequest.getEmail(), emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(accountRequest.getRegistrationUrl()));
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
