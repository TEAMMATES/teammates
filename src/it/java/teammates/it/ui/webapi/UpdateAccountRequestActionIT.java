package teammates.it.ui.webapi;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelperExtension;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateAccountRequestAction;

/**
 * SUT: {@link UpdateAccountRequestAction}.
 */
public class UpdateAccountRequestActionIT extends BaseActionIT<UpdateAccountRequestAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        // no need to call super.setUp() because the action handles its own transactions
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        ______TS("edit fields of an account request");
        AccountRequest accountRequest = logic.createAccountRequestWithTransaction("name", "email@email.com",
                "institute", AccountRequestStatus.PENDING, "comments");
        UUID id = accountRequest.getId();
        String name = "newName";
        String email = "newEmail@email.com";
        String institute = "newInstitute";
        String comments = "newComments";
        AccountRequestStatus status = accountRequest.getStatus();

        AccountRequestUpdateRequest requestBody = new AccountRequestUpdateRequest(name, email, institute, status, comments);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        UpdateAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = action.execute();

        assertEquals(result.getStatusCode(), 200);
        AccountRequestData data = (AccountRequestData) result.getOutput();

        assertEquals(name, data.getName());
        assertEquals(email, data.getEmail());
        assertEquals(institute, data.getInstitute());
        assertEquals(status, data.getStatus());
        assertEquals(comments, data.getComments());
        verifyNoEmailsSent();

        ______TS("approve a pending account request");
        accountRequest = logic.createAccountRequestWithTransaction("name", "email@email.com",
                "institute", AccountRequestStatus.PENDING, "comments");
        requestBody = new AccountRequestUpdateRequest(accountRequest.getName(), accountRequest.getEmail(),
                accountRequest.getInstitute(), AccountRequestStatus.APPROVED, accountRequest.getComments());
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};
        action = getAction(requestBody, params);
        result = getJsonResult(action, 200);
        data = (AccountRequestData) result.getOutput();

        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(AccountRequestStatus.APPROVED, data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());
        verifyNumberOfEmailsSent(1);

        ______TS("already registered account request has no email sent when approved");
        accountRequest = logic.createAccountRequestWithTransaction("name", "email@email.com",
                "institute", AccountRequestStatus.REGISTERED, "comments");
        requestBody = new AccountRequestUpdateRequest(name, email, institute, AccountRequestStatus.APPROVED, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        action = getAction(requestBody, params);
        result = getJsonResult(action, 200);
        data = (AccountRequestData) result.getOutput();

        assertEquals(name, data.getName());
        assertEquals(email, data.getEmail());
        assertEquals(institute, data.getInstitute());
        assertEquals(AccountRequestStatus.REGISTERED, data.getStatus());
        assertEquals(comments, data.getComments());
        verifyNumberOfEmailsSent(0);

        ______TS("email with existing account throws exception");
        Account account = logic.createAccountWithTransaction(getTypicalAccount());
        accountRequest = logic.createAccountRequestWithTransaction("name", account.getEmail(),
                "institute", AccountRequestStatus.PENDING, "comments");
        requestBody = new AccountRequestUpdateRequest(name, email, institute, AccountRequestStatus.APPROVED, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        InvalidOperationException ipe = verifyInvalidOperation(requestBody, params);

        assertEquals(String.format("An account with email %s already exists. "
                + "Please reject or delete the account request instead.", account.getEmail()), ipe.getMessage());

        ______TS("non-existent but valid uuid");
        requestBody = new AccountRequestUpdateRequest("name", "email",
                "institute", AccountRequestStatus.PENDING, "comments");
        String validUuid = UUID.randomUUID().toString();
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, validUuid};

        EntityNotFoundException enfe = verifyEntityNotFound(requestBody, params);

        assertEquals(String.format("Account request with id = %s not found", validUuid), enfe.getMessage());

        ______TS("invalid uuid");
        requestBody = new AccountRequestUpdateRequest("name", "email",
                "institute", AccountRequestStatus.PENDING, "comments");
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, "invalid"};

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(requestBody, params);

        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());

        ______TS("invalid email");
        accountRequest = logic.createAccountRequestWithTransaction("name", "email@email.com",
                "institute", AccountRequestStatus.PENDING, "comments");
        id = accountRequest.getId();
        email = "newEmail";
        status = accountRequest.getStatus();

        requestBody = new AccountRequestUpdateRequest(name, email, institute, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, email,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT, FieldValidator.EMAIL_MAX_LENGTH),
                ihrbe.getMessage());

        ______TS("invalid name alphanumeric");
        name = "@$@#$#@#@$#@$";
        email = "newEmail@email.com";

        requestBody = new AccountRequestUpdateRequest(name, email, institute, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, name,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR),
                ihrbe.getMessage());

        ______TS("invalid name too long");
        name = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);

        requestBody = new AccountRequestUpdateRequest(name, email, institute, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, name,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.PERSON_NAME_MAX_LENGTH), ihrbe.getMessage());

        ______TS("null email value");
        name = "newName";

        requestBody = new AccountRequestUpdateRequest(name, null, institute, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("email cannot be null", ihrbe.getMessage());

        ______TS("null name value");
        requestBody = new AccountRequestUpdateRequest(null, email, institute, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("name cannot be null", ihrbe.getMessage());

        ______TS("null status value");
        requestBody = new AccountRequestUpdateRequest(name, email, institute, null, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("status cannot be null", ihrbe.getMessage());

        ______TS("null institute value");
        requestBody = new AccountRequestUpdateRequest(name, email, null, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("institute cannot be null", ihrbe.getMessage());

        ______TS("allow null comments in request");
        requestBody = new AccountRequestUpdateRequest(name, email, institute, status, null);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        action = getAction(requestBody, params);
        result = getJsonResult(action, 200);
        data = (AccountRequestData) result.getOutput();

        assertEquals(name, data.getName());
        assertEquals(email, data.getEmail());
        assertEquals(institute, data.getInstitute());
        assertEquals(null, data.getComments());

        ______TS("email with approved account request throws exception");
        logic.createAccountRequestWithTransaction("test", "test@email.com",
                "institute", AccountRequestStatus.APPROVED, "comments");
        accountRequest = logic.createAccountRequestWithTransaction("test", "test@email.com",
                "institute", AccountRequestStatus.PENDING, "comments");
        requestBody = new AccountRequestUpdateRequest(accountRequest.getName(), accountRequest.getEmail(),
                accountRequest.getInstitute(), AccountRequestStatus.APPROVED, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.getId().toString()};

        ipe = verifyInvalidOperation(requestBody, params);

        assertEquals(String.format("An account request with email %s has already been approved. "
                + "Please reject or delete the account request instead.", accountRequest.getEmail()), ipe.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        verifyOnlyAdminCanAccessWithTransaction();
    }

    @Override
    @AfterMethod
    protected void tearDown() {
        HibernateUtil.beginTransaction();
        List<AccountRequest> accountRequests = logic.getAllAccountRequests();
        for (AccountRequest ar : accountRequests) {
            logic.deleteAccountRequest(ar.getId());
        }

        logic.deleteAccount(getTypicalAccount().getGoogleId());
        HibernateUtil.commitTransaction();
    }
}
