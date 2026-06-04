package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountRequestData;
import teammates.ui.webapi.ApproveAccountRequestAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link ApproveAccountRequestAction}.
 */
public class ApproveAccountRequestActionIT extends BaseActionIT<ApproveAccountRequestAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST_APPROVAL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() {
        // This is separated into different test methods.
    }

    @Test
    void testExecute_pendingRequest_approvesSuccessfully() {
        AccountRequest accountRequest = inTransaction(() -> logic.createAccountRequest("name", "pending@email.com",
                "institute", AccountRequestStatus.PENDING, "comments"));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        ApproveAccountRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_rejectedRequest_approvesSuccessfully() {
        AccountRequest accountRequest = inTransaction(() -> logic.createAccountRequest("name", "rejected@email.com",
                "institute", AccountRequestStatus.REJECTED, "comments"));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        ApproveAccountRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_existingAccountWithSameEmail_approvesSuccessfully() {
        Account existingAccount = getTypicalAccount();
        existingAccount.setEmail("existing@email.com");
        inTransaction(() -> logic.createAccount(
                existingAccount.getProvider(), existingAccount.getSubject(), existingAccount.getTenantId(),
                existingAccount.getEmail(), existingAccount.getGoogleId()));

        AccountRequest accountRequest = inTransaction(() -> logic.createAccountRequest("name", existingAccount.getEmail(),
                "anotherInstitute", AccountRequestStatus.PENDING, "comments"));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        ApproveAccountRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_existingApprovedRequestWithSameEmailDifferentInstitute_approvesSuccessfully() {
        inTransaction(() -> logic.createAccountRequest("name", "same@email.com",
                "instituteA", AccountRequestStatus.APPROVED, "comments"));
        AccountRequest accountRequest = inTransaction(() -> logic.createAccountRequest("name", "same@email.com",
                "instituteB", AccountRequestStatus.PENDING, "comments"));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        ApproveAccountRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());
        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_invalidUuid_throwsInvalidHttpParameterException() {
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, "invalid"};
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_nonExistentUuid_throwsEntityNotFoundException() {
        String uuid = UUID.randomUUID().toString();
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, uuid};
        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals(String.format("Account request with id = %s not found", uuid), enfe.getMessage());
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_invalidStatus_throwsInvalidOperationException() {
        AccountRequest accountRequest = inTransaction(() -> logic.createAccountRequest("name", "registered@email.com",
                "institute", AccountRequestStatus.REGISTERED, "comments"));
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        InvalidOperationException ipe = verifyInvalidOperation(params);
        assertEquals("Account request with id " + accountRequest.getId()
                + " is already approved or registered.", ipe.getMessage());
        verifyNoEmailsSent();
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
