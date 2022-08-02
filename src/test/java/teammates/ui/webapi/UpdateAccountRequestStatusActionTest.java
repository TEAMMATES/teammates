package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestStatusUpdateIntent;

/**
 * SUT: {@link UpdateAccountRequestStatusAction}.
 */
public class UpdateAccountRequestStatusActionTest extends BaseActionTest<UpdateAccountRequestStatusAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST_STATUS;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    protected void testExecute_approveAccountRequest() {
        AccountRequestAttributes submittedAccountRequest =
                logic.getAccountRequest("submittedInstructor2@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes approvedAccountRequest =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes rejectedAccountRequest =
                logic.getAccountRequest("rejectedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes registeredAccountRequest =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1");

        ______TS("success: approve account request with status SUBMITTED");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, submittedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, submittedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_APPROVE.toString(),
        };
        UpdateAccountRequestStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        AccountRequestAttributes actualAccountRequest =
                logic.getAccountRequest(submittedAccountRequest.getEmail(), submittedAccountRequest.getInstitute());
        assertEquals(submittedAccountRequest.getName(), actualAccountRequest.getName());
        assertEquals(submittedAccountRequest.getInstitute(), actualAccountRequest.getInstitute());
        assertEquals(submittedAccountRequest.getEmail(), actualAccountRequest.getEmail());
        assertEquals(submittedAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(submittedAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.APPROVED, actualAccountRequest.getStatus());
        assertEquals(submittedAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertNotNull(actualAccountRequest.getLastProcessedAt());
        assertNotEquals(submittedAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertEquals(submittedAccountRequest.getRegisteredAt(), actualAccountRequest.getRegisteredAt());
        assertEquals(submittedAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), actualAccountRequest.getName()),
                emailSent.getSubject());
        assertEquals(actualAccountRequest.getEmail(), emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(actualAccountRequest.getRegistrationUrl()));

        ______TS("success: approve account request with status REJECTED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, rejectedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, rejectedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_APPROVE.toString(),
        };
        action = getAction(params);
        result = getJsonResult(action);

        actualAccountRequest =
                logic.getAccountRequest(rejectedAccountRequest.getEmail(), rejectedAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.APPROVED, actualAccountRequest.getStatus());

        output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNumberOfEmailsSent(1);
        emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), actualAccountRequest.getName()),
                emailSent.getSubject());
        assertEquals(actualAccountRequest.getEmail(), emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(actualAccountRequest.getRegistrationUrl()));

        ______TS("success: approve account request with status APPROVED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, approvedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, approvedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_APPROVE.toString(),
        };
        action = getAction(params);
        result = getJsonResult(action);

        actualAccountRequest =
                logic.getAccountRequest(approvedAccountRequest.getEmail(), approvedAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.APPROVED, actualAccountRequest.getStatus());

        output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNumberOfEmailsSent(1);
        emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), actualAccountRequest.getName()),
                emailSent.getSubject());
        assertEquals(actualAccountRequest.getEmail(), emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(actualAccountRequest.getRegistrationUrl()));

        ______TS("failure: approve account request with status REGISTERED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, registeredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, registeredAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_APPROVE.toString(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Account request of a registered instructor cannot be approved.", ioe.getMessage());

        actualAccountRequest =
                logic.getAccountRequest(registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.REGISTERED, actualAccountRequest.getStatus());

        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_rejectAccountRequest() {
        AccountRequestAttributes submittedAccountRequest =
                logic.getAccountRequest("submittedInstructor2@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes approvedAccountRequest =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes rejectedAccountRequest =
                logic.getAccountRequest("rejectedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes registeredAccountRequest =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1");

        ______TS("success: reject account request with status SUBMITTED");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, submittedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, submittedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_REJECT.toString(),
        };
        UpdateAccountRequestStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        AccountRequestAttributes actualAccountRequest =
                logic.getAccountRequest(submittedAccountRequest.getEmail(), submittedAccountRequest.getInstitute());
        assertEquals(submittedAccountRequest.getName(), actualAccountRequest.getName());
        assertEquals(submittedAccountRequest.getInstitute(), actualAccountRequest.getInstitute());
        assertEquals(submittedAccountRequest.getEmail(), actualAccountRequest.getEmail());
        assertEquals(submittedAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(submittedAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.REJECTED, actualAccountRequest.getStatus());
        assertEquals(submittedAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertNotNull(actualAccountRequest.getLastProcessedAt());
        assertNotEquals(submittedAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertEquals(submittedAccountRequest.getRegisteredAt(), actualAccountRequest.getRegisteredAt());
        assertEquals(submittedAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNoEmailsSent();

        ______TS("success: reject account request with status APPROVED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, approvedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, approvedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_REJECT.toString(),
        };
        action = getAction(params);
        result = getJsonResult(action);

        actualAccountRequest =
                logic.getAccountRequest(approvedAccountRequest.getEmail(), approvedAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.REJECTED, actualAccountRequest.getStatus());

        output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNoEmailsSent();

        ______TS("success: reject account request with status REJECTED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, rejectedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, rejectedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_REJECT.toString(),
        };
        action = getAction(params);
        result = getJsonResult(action);

        actualAccountRequest =
                logic.getAccountRequest(rejectedAccountRequest.getEmail(), rejectedAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.REJECTED, actualAccountRequest.getStatus());

        output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNoEmailsSent();

        ______TS("failure: approve account request with status REGISTERED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, registeredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, registeredAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_REJECT.toString(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Account request of a registered instructor cannot be rejected.", ioe.getMessage());

        actualAccountRequest =
                logic.getAccountRequest(registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.REGISTERED, actualAccountRequest.getStatus());

        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_resetAccountRequest() {
        AccountRequestAttributes submittedAccountRequest =
                logic.getAccountRequest("submittedInstructor2@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes approvedAccountRequest =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes rejectedAccountRequest =
                logic.getAccountRequest("rejectedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes registeredAccountRequest =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1");

        ______TS("success: reset account request with status REJECTED");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, rejectedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, rejectedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_RESET.toString(),
        };
        UpdateAccountRequestStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        AccountRequestAttributes actualAccountRequest =
                logic.getAccountRequest(rejectedAccountRequest.getEmail(), rejectedAccountRequest.getInstitute());
        assertEquals(rejectedAccountRequest.getName(), actualAccountRequest.getName());
        assertEquals(rejectedAccountRequest.getInstitute(), actualAccountRequest.getInstitute());
        assertEquals(rejectedAccountRequest.getEmail(), actualAccountRequest.getEmail());
        assertEquals(rejectedAccountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(rejectedAccountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());
        assertEquals(rejectedAccountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertNotNull(actualAccountRequest.getLastProcessedAt());
        assertNotEquals(rejectedAccountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertEquals(rejectedAccountRequest.getRegisteredAt(), actualAccountRequest.getRegisteredAt());
        assertEquals(rejectedAccountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNoEmailsSent();

        ______TS("success: reset account request with status REGISTERED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, registeredAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, registeredAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_RESET.toString(),
        };
        action = getAction(params);
        result = getJsonResult(action);

        actualAccountRequest =
                logic.getAccountRequest(registeredAccountRequest.getEmail(), registeredAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());

        output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNoEmailsSent();

        ______TS("success: reset account request with status SUBMITTED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, submittedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, submittedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_RESET.toString(),
        };
        action = getAction(params);
        result = getJsonResult(action);

        actualAccountRequest =
                logic.getAccountRequest(submittedAccountRequest.getEmail(), submittedAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());

        output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifyNoEmailsSent();

        ______TS("failure: reset account request with status APPROVED");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, approvedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, approvedAccountRequest.getInstitute(),
                Const.ParamsNames.INTENT, AccountRequestStatusUpdateIntent.TO_RESET.toString(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Account requests with status APPROVED cannot be reset."
                + " Reject it first and then reset.", ioe.getMessage());

        actualAccountRequest =
                logic.getAccountRequest(approvedAccountRequest.getEmail(), approvedAccountRequest.getInstitute());
        assertEquals(AccountRequestStatus.APPROVED, actualAccountRequest.getStatus());

        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_nonExistentAccountRequest_throwException() {
        ______TS("failure: account request to update status does not exist");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent@email",
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, "Non-existent Institute",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Account request with email: non-existent@email"
                + " and institute: Non-existent Institute does not exist.", enfe.getMessage());
    }

    @Test
    protected void testExecute_nullParameters_throwException() {
        ______TS("failure: null parameters");

        AccountRequestAttributes accountRequest =
                logic.getAccountRequest("submittedInstructor1@tmt.tmt", "TMT, Singapore");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_EMAIL),
                ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_INSTITUTION),
                ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INTENT),
                ihpe.getMessage());
    }

    @Override
    @Test
    protected void testExecute() {
        // see individual tests
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
