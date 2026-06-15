package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;

/**
 * SUT: {@link ApproveAccountVerificationRequestAction}.
 */
public class ApproveAccountVerificationRequestActionIT extends BaseActionIT<ApproveAccountVerificationRequestAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_VERIFICATION_REQUEST_APPROVAL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() {
        // This is separated into different test methods.
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_pendingRequest_approvesSuccessfully() {
        UUID accountId = typicalBundle.accounts.get("instructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                "name", "pending@email.com", "institute", "SG",
                AccountVerificationRequestStatus.PENDING, "comments", accountId));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID,
                accountVerificationRequest.getId().toString()};

        ApproveAccountVerificationRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountVerificationRequestData data = (AccountVerificationRequestData) result.getOutput();
        assertEquals(accountVerificationRequest.getName(), data.getName());
        assertEquals(accountVerificationRequest.getEmail(), data.getEmail());
        assertEquals(accountVerificationRequest.getInstitute().getName(), data.getInstitute());
        assertEquals(AccountVerificationRequestStatus.APPROVED, data.getStatus());
        assertEquals(accountVerificationRequest.getComments(), data.getComments());
        verifyNumberOfEmailsSent(1);
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_rejectedRequest_approvesSuccessfully() {
        UUID accountId = typicalBundle.accounts.get("instructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                "name", "rejected@email.com", "institute", "SG",
                AccountVerificationRequestStatus.REJECTED, "comments", accountId));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID,
                accountVerificationRequest.getId().toString()};

        ApproveAccountVerificationRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountVerificationRequestData data = (AccountVerificationRequestData) result.getOutput();
        assertEquals(AccountVerificationRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_existingAccountWithSameEmail_approvesSuccessfully() {
        Account existingAccount = getTypicalAccount();
        existingAccount.setEmail("existing@email.com");
        inTransaction(() -> logic.createAccount(
                existingAccount.getProvider(), existingAccount.getSubject(), existingAccount.getTenantId(),
                existingAccount.getEmail(), existingAccount.getGoogleId()));

        UUID accountId = typicalBundle.accounts.get("instructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                "name", existingAccount.getEmail(), "anotherInstitute", "SG",
                AccountVerificationRequestStatus.PENDING, "comments", accountId));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID,
                accountVerificationRequest.getId().toString()};

        ApproveAccountVerificationRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountVerificationRequestData data = (AccountVerificationRequestData) result.getOutput();
        assertEquals(AccountVerificationRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_existingApprovedRequestWithSameEmailDifferentInstitute_approvesSuccessfully() {
        UUID accountId = typicalBundle.accounts.get("instructor1").getId();
        inTransaction(() -> logic.createAccountVerificationRequest("name", "same@email.com",
                "instituteA", "SG", AccountVerificationRequestStatus.APPROVED, "comments", accountId));
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                "name", "same@email.com", "instituteB", "SG",
                AccountVerificationRequestStatus.PENDING, "comments", accountId));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID,
                accountVerificationRequest.getId().toString()};

        ApproveAccountVerificationRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountVerificationRequestData data = (AccountVerificationRequestData) result.getOutput();
        assertEquals(AccountVerificationRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_invalidUuid_throwsInvalidHttpParameterException() {
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, "invalid"};
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());
        verifyNoEmailsSent();
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_nonExistentUuid_throwsEntityNotFoundException() {
        String uuid = UUID.randomUUID().toString();
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, uuid};
        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals(String.format("Account verification request with id = %s not found", uuid), enfe.getMessage());
        verifyNoEmailsSent();
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_invalidStatus_throwsInvalidOperationException() {
        UUID accountId = typicalBundle.accounts.get("instructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                "name", "registered@email.com", "institute", "SG",
                AccountVerificationRequestStatus.APPROVED, "comments", accountId));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID,
                accountVerificationRequest.getId().toString()};

        InvalidOperationException ipe = verifyInvalidOperation(params);
        assertEquals("Account verification request with id " + accountVerificationRequest.getId()
                + " is already approved.", ipe.getMessage());
        verifyNoEmailsSent();
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
