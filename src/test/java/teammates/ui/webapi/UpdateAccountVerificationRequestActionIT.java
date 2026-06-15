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
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountVerificationRequestUpdateRequest;

/**
 * SUT: {@link UpdateAccountVerificationRequestAction}.
 */
public class UpdateAccountVerificationRequestActionIT extends BaseActionIT<UpdateAccountVerificationRequestAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_VERIFICATION_REQUEST;
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
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest("name", "email@email.com",
                "institute", "SG", AccountVerificationRequestStatus.PENDING, "comments", accountId));
        UUID id = accountVerificationRequest.getId();
        String name = "newName";
        String email = "newemail@email.com";
        String institute = "newInstitute";
        String country = "SG";
        String comments = "newComments";
        AccountVerificationRequestStatus status = accountVerificationRequest.getStatus();

        AccountVerificationRequestUpdateRequest requestBody =
                new AccountVerificationRequestUpdateRequest(name, email, institute, country, status, comments);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        UpdateAccountVerificationRequestAction action = getAction(requestBody, params);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), 200);
        AccountVerificationRequestData data = (AccountVerificationRequestData) result.getOutput();

        assertEquals(name, data.getName());
        assertEquals(email, data.getEmail());
        assertEquals(institute, data.getInstitute());
        assertEquals(status, data.getStatus());
        assertEquals(comments, data.getComments());
        verifyNoEmailsSent();

        ______TS("non-existent but valid uuid");
        requestBody = new AccountVerificationRequestUpdateRequest("name", "email",
                "institute", "SG", AccountVerificationRequestStatus.PENDING, "comments");
        String validUuid = UUID.randomUUID().toString();
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, validUuid};

        EntityNotFoundException enfe = verifyEntityNotFound(requestBody, params);

        assertEquals(String.format("Account request with id = %s not found", validUuid), enfe.getMessage());

        ______TS("invalid uuid");
        requestBody = new AccountVerificationRequestUpdateRequest("name", "email",
                "institute", "SG", AccountVerificationRequestStatus.PENDING, "comments");
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, "invalid"};

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(requestBody, params);

        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());

        ______TS("invalid email");
        accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest("name", "email@email.com",
                "institute", "SG", AccountVerificationRequestStatus.PENDING, "comments", accountId));
        id = accountVerificationRequest.getId();
        email = "newemail";
        status = accountVerificationRequest.getStatus();

        requestBody = new AccountVerificationRequestUpdateRequest(name, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, email,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT, FieldValidator.EMAIL_MAX_LENGTH),
                ihrbe.getMessage());

        ______TS("invalid name alphanumeric");
        name = "@$@#$#@#@$#@$";
        email = "newemail@email.com";

        requestBody = new AccountVerificationRequestUpdateRequest(name, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, name,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR),
                ihrbe.getMessage());

        ______TS("invalid name too long");
        name = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);

        requestBody = new AccountVerificationRequestUpdateRequest(name, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals(getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, name,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.PERSON_NAME_MAX_LENGTH), ihrbe.getMessage());

        ______TS("null email value");
        name = "newName";

        requestBody = new AccountVerificationRequestUpdateRequest(name, null, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("email cannot be null", ihrbe.getMessage());

        ______TS("null name value");
        requestBody = new AccountVerificationRequestUpdateRequest(null, email, institute, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("name cannot be null", ihrbe.getMessage());

        ______TS("null status value");
        requestBody = new AccountVerificationRequestUpdateRequest(name, email, institute, country, null, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("status cannot be null", ihrbe.getMessage());

        ______TS("null institute value");
        requestBody = new AccountVerificationRequestUpdateRequest(name, email, null, country, status, comments);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("institute cannot be null", ihrbe.getMessage());

        ______TS("allow null comments in request");
        requestBody = new AccountVerificationRequestUpdateRequest(name, email, institute, country, status, null);
        params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        action = getAction(requestBody, params);
        result = getJsonResult(action, 200);
        data = (AccountVerificationRequestData) result.getOutput();

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
