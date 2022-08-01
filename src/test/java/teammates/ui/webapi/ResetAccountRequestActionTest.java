package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
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
        AccountRequestAttributes registeredAccountRequest =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1");
        AccountRequestAttributes unregisteredAccountRequest =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore");

        ______TS("typical success case");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, registeredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, registeredAccountRequest.getInstitute(),
        };
        ResetAccountRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        AccountRequestAttributes actualAccountRequest =
                logic.getAccountRequest(registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute());

        assertEquals(registeredAccountRequest.getName(), actualAccountRequest.getName());
        assertEquals(registeredAccountRequest.getInstitute(), actualAccountRequest.getInstitute());
        assertEquals(registeredAccountRequest.getEmail(), actualAccountRequest.getEmail());
        assertEquals(registeredAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(registeredAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.APPROVED, actualAccountRequest.getStatus());
        assertEquals(registeredAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertNotNull(actualAccountRequest.getLastProcessedAt());
        assertNotEquals(registeredAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertNull(actualAccountRequest.getRegisteredAt());
        assertEquals(registeredAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        String joinLink = actualAccountRequest.getRegistrationUrl();
        JoinLinkData output = (JoinLinkData) result.getOutput();
        assertEquals(joinLink, output.getJoinLink());

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), registeredAccountRequest.getName()),
                emailSent.getSubject());
        assertEquals(registeredAccountRequest.getEmail(), emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(joinLink));

        ______TS("failure: reset account whose status is not REGISTERED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, unregisteredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, unregisteredAccountRequest.getInstitute(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Unable to reset account request as instructor is still unregistered.", ioe.getMessage());

        ______TS("failure: account request to reset does not exist");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent@email",
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, "TMT, Singapore",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Account request with email: non-existent@email"
                + " and institute: TMT, Singapore does not exist.", enfe.getMessage());

        ______TS("failure: null parameters");

        registeredAccountRequest = logic.getAccountRequest("instr1@course2.tmt", "TEAMMATES Test Institute 1");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, registeredAccountRequest.getInstitute(),
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_EMAIL),
                ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, registeredAccountRequest.getEmail(),
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_INSTITUTION),
                ihpe.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
