package teammates.it.ui.webapi;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.AccountRequestData;
import teammates.ui.webapi.ApproveAccountRequestAction;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link ApproveAccountRequestAction}.
 */
public class ApproveAccountRequestActionIT extends BaseActionIT<ApproveAccountRequestAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
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
    @Test
    public void testExecute() throws Exception {
        ______TS("approve pending request should succeed and send email");
        AccountRequest accountRequest = logic.createAccountRequest("name", "pending@email.com",
                "institute", AccountRequestStatus.PENDING, "comments");
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        ApproveAccountRequestAction action = getAction(params);
        JsonResult result = action.execute();

        assertEquals(200, result.getStatusCode());
        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());
        verifyNumberOfEmailsSent(1);

        ______TS("approve rejected request should succeed and send email");
        accountRequest = logic.createAccountRequest("name", "rejected@email.com",
                "institute", AccountRequestStatus.REJECTED, "comments");
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        action = getAction(params);
        result = action.execute();

        assertEquals(200, result.getStatusCode());
        data = (AccountRequestData) result.getOutput();
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);

        ______TS("existing account with same email should not block approval");
        Account existingAccount = getTypicalAccount();
        existingAccount.setEmail("existing@email.com");
        logic.createAccount(existingAccount);

        accountRequest = logic.createAccountRequest("name", existingAccount.getEmail(),
                "anotherInstitute", AccountRequestStatus.PENDING, "comments");
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        action = getAction(params);
        result = action.execute();

        assertEquals(200, result.getStatusCode());
        data = (AccountRequestData) result.getOutput();
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);

        ______TS("same email different institute with existing approved request should succeed");
        logic.createAccountRequest("name", "same@email.com",
                "instituteA", AccountRequestStatus.APPROVED, "comments");
        accountRequest = logic.createAccountRequest("name", "same@email.com",
                "instituteB", AccountRequestStatus.PENDING, "comments");
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        action = getAction(params);
        result = action.execute();

        assertEquals(200, result.getStatusCode());
        data = (AccountRequestData) result.getOutput();
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        verifyNumberOfEmailsSent(1);

        ______TS("same email and institute with existing approved request should fail");
        logic.createAccountRequest("name", "duplicate@email.com",
                "dupInstitute", AccountRequestStatus.APPROVED, "comments");
        accountRequest = logic.createAccountRequest("name", "duplicate@email.com",
                "dupInstitute", AccountRequestStatus.PENDING, "comments");
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        InvalidOperationException ipe = verifyInvalidOperation(params);
        assertEquals(String.format("An account request with email %s and institute %s has already been approved. "
                + "Please reject or delete the account request instead.",
                accountRequest.getEmail(), accountRequest.getInstitute()), ipe.getMessage());
        verifyNoEmailsSent();

        ______TS("invalid uuid should fail");
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, "invalid"};
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());
        verifyNoEmailsSent();

        ______TS("non-existent uuid should fail");
        String uuid = UUID.randomUUID().toString();
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, uuid};
        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals(String.format("Account request with id = %s not found", uuid), enfe.getMessage());
                verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_invalidStatusShouldFail() throws InvalidParametersException {
        AccountRequest accountRequest = logic.createAccountRequest("name", "registered@email.com",
                "institute", AccountRequestStatus.REGISTERED, "comments");
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        InvalidOperationException ipe = verifyInvalidOperation(params);
        assertEquals("Account request with id " + accountRequest.getId()
                + " is not pending or rejected and cannot be approved.", ipe.getMessage());
        verifyNoEmailsSent();
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
