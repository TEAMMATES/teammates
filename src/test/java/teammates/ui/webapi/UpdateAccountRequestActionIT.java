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
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestUpdateRequest;

/**
 * SUT: {@link UpdateAccountRequestAction}.
 */
public class UpdateAccountRequestActionIT extends BaseActionIT<UpdateAccountRequestAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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
    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute() throws Exception {
        ______TS("edit fields of an account request");
        UUID accountId = typicalBundle.accounts.get("instructor1").getId();
        AccountRequest accountRequest = inTransaction(() -> logic.createAccountRequest("name", "email@email.com",
                "institute", "SG", AccountRequestStatus.PENDING, "comments", accountId));
        UUID id = accountRequest.getId();
        String name = "newName";
        String email = "newemail@email.com";
        String institute = "newInstitute";
        String country = "SG";
        String comments = "newComments";
        AccountRequestStatus status = accountRequest.getStatus();

        AccountRequestUpdateRequest requestBody =
                new AccountRequestUpdateRequest(name, email, institute, country, status, comments);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        UpdateAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), 200);
        AccountRequestData data = (AccountRequestData) result.getOutput();

        assertEquals(name, data.getName());
        assertEquals(email, data.getEmail());
        assertEquals(institute, data.getInstitute());
        assertEquals(status, data.getStatus());
        assertEquals(comments, data.getComments());
        verifyNoEmailsSent();

        ______TS("non-existent but valid uuid");
        requestBody = new AccountRequestUpdateRequest("name", "email",
                "institute", "SG", AccountRequestStatus.PENDING, "comments");
        String validUuid = UUID.randomUUID().toString();
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, validUuid};

        EntityNotFoundException enfe = verifyEntityNotFound(requestBody, params);

        assertEquals(String.format("Account request with id = %s not found", validUuid), enfe.getMessage());

        ______TS("invalid uuid");
        requestBody = new AccountRequestUpdateRequest("name", "email",
                "institute", "SG", AccountRequestStatus.PENDING, "comments");
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, "invalid"};

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(requestBody, params);

        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());

        ______TS("invalid email");
        accountRequest = inTransaction(() -> logic.createAccountRequest("name", "email@email.com",
                "institute", "SG", AccountRequestStatus.PENDING, "comments", accountId));
        id = accountRequest.getId();
        email = "newemail";
        status = accountRequest.getStatus();

        requestBody = new AccountRequestUpdateRequest(name, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, email,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT, FieldValidator.EMAIL_MAX_LENGTH),
                ihrbe.getMessage());

        ______TS("invalid name alphanumeric");
        name = "@$@#$#@#@$#@$";
        email = "newemail@email.com";

        requestBody = new AccountRequestUpdateRequest(name, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, name,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR),
                ihrbe.getMessage());

        ______TS("invalid name too long");
        name = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);

        requestBody = new AccountRequestUpdateRequest(name, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, name,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.PERSON_NAME_MAX_LENGTH), ihrbe.getMessage());

        ______TS("null email value");
        name = "newName";

        requestBody = new AccountRequestUpdateRequest(name, null, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("email cannot be null", ihrbe.getMessage());

        ______TS("null name value");
        requestBody = new AccountRequestUpdateRequest(null, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("name cannot be null", ihrbe.getMessage());

        ______TS("null status value");
        requestBody = new AccountRequestUpdateRequest(name, email, institute, country, null, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("status cannot be null", ihrbe.getMessage());

        ______TS("null institute value");
        requestBody = new AccountRequestUpdateRequest(name, email, null, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("institute cannot be null", ihrbe.getMessage());

        ______TS("allow null comments in request");
        requestBody = new AccountRequestUpdateRequest(name, email, institute, country, status, null);
        params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        action = getAction(requestBody, params);
        result = getJsonResult(action, 200);
        data = (AccountRequestData) result.getOutput();

        assertEquals(name, data.getName());
        assertEquals(email, data.getEmail());
        assertEquals(institute, data.getInstitute());
        assertEquals(null, data.getComments());
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
