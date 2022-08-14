package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link UpdateAccountRequestAction}.
 */
public class UpdateAccountRequestActionTest extends BaseActionTest<UpdateAccountRequestAction> {

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
    protected void testExecute() throws Exception {
        AccountRequestAttributes accountRequest =
                logic.getAccountRequest("submittedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes submittedAccountRequest =
                logic.getAccountRequest("submittedInstructor2@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes approvedAccountRequest =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes rejectedAccountRequest =
                logic.getAccountRequest("rejectedInstructor1@tmt.tmt", "TMT, Singapore");
        AccountRequestAttributes registeredAccountRequest =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1");

        ______TS("typical success case");

        String newName = "Submitted Instructor 1 Updated";
        String newInstitute = "TEAMMATES Test Institute, Singapore";
        String newEmail = "submittedInstructor1Updated@tmt.tmt";

        String newNameWithSpaces = "   Submitted    Instructor     1     Updated  ";
        String newInstituteWithSpaces = "   TEAMMATES  Test    Institute,   Singapore  ";
        String newEmailWithSpaces = "   " + newEmail + "   ";

        AccountRequestUpdateRequest req = buildUpdateRequest(newNameWithSpaces, newInstituteWithSpaces, newEmailWithSpaces);
        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        UpdateAccountRequestAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);

        AccountRequestAttributes actualAccountRequest = logic.getAccountRequest(newEmail, newInstitute);

        assertEquals(newName, actualAccountRequest.getName());
        assertEquals(newInstitute, actualAccountRequest.getInstitute());
        assertEquals(newEmail, actualAccountRequest.getEmail());
        assertEquals(accountRequest.getHomePageUrl(), actualAccountRequest.getHomePageUrl());
        assertEquals(accountRequest.getComments(), actualAccountRequest.getComments());
        assertEquals(accountRequest.getStatus(), actualAccountRequest.getStatus());
        assertEquals(accountRequest.getCreatedAt(), actualAccountRequest.getCreatedAt());
        assertNotNull(actualAccountRequest.getLastProcessedAt());
        assertNotEquals(accountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());
        assertEquals(accountRequest.getRegisteredAt(), actualAccountRequest.getRegisteredAt());
        assertEquals(accountRequest.getRegistrationKey(), actualAccountRequest.getRegistrationKey());

        assertNull(logic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()));

        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("lastProcessedAt is still updated when no fields are changed");

        accountRequest = actualAccountRequest;

        req = buildUpdateRequest(accountRequest.getName(), accountRequest.getInstitute(), accountRequest.getEmail());
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        action = getAction(req, params);
        result = getJsonResult(action);

        actualAccountRequest = logic.getAccountRequest(newEmail, newInstitute);

        assertNotEquals(accountRequest.getLastProcessedAt(), actualAccountRequest.getLastProcessedAt());

        output = (AccountRequestData) result.getOutput();
        assertEquals(new AccountRequestData(actualAccountRequest), output);

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("failure: account request to update does not exist");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent@email",
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Account request with email: non-existent@email"
                + " and institute: TEAMMATES Test Institute, Singapore does not exist.", enfe.getMessage());

        verifyNoTasksAdded();

        ______TS("failure: account request to update to already exists");

        req = buildUpdateRequest(accountRequest.getName(),
                submittedAccountRequest.getInstitute(), submittedAccountRequest.getEmail());
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        InvalidOperationException ioe = verifyInvalidOperation(req, params);
        assertEquals("There’s an existing account request with the email address and institute"
                        + " you want to update to, and its status is SUBMITTED.",
                ioe.getMessage());
        verifyNoTasksAdded();

        req = buildUpdateRequest(accountRequest.getName(),
                approvedAccountRequest.getInstitute(), approvedAccountRequest.getEmail());
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        ioe = verifyInvalidOperation(req, params);
        assertEquals("There’s an existing account request with the email address and institute"
                        + " you want to update to, and its status is APPROVED.",
                ioe.getMessage());
        verifyNoTasksAdded();

        req = buildUpdateRequest(accountRequest.getName(),
                rejectedAccountRequest.getInstitute(), rejectedAccountRequest.getEmail());
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        ioe = verifyInvalidOperation(req, params);
        assertEquals("There’s an existing account request with the email address and institute"
                        + " you want to update to, and its status is REJECTED.",
                ioe.getMessage());
        verifyNoTasksAdded();

        req = buildUpdateRequest(accountRequest.getName(),
                registeredAccountRequest.getInstitute(), registeredAccountRequest.getEmail());
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        ioe = verifyInvalidOperation(req, params);
        assertEquals("There’s an existing account request with the email address and institute"
                        + " you want to update to, and its status is REGISTERED.",
                ioe.getMessage());
        verifyNoTasksAdded();

        ______TS("failure: edit account request whose status is not SUBMITTED");

        req = buildUpdateRequest(newNameWithSpaces, newInstituteWithSpaces, newEmailWithSpaces);
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, approvedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, approvedAccountRequest.getInstitute(),
        };

        ioe = verifyInvalidOperation(req, params);
        assertEquals("Only account requests with status SUBMITTED can be edited.", ioe.getMessage());

        verifyNoTasksAdded();

        ______TS("failure: invalid body fields");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, submittedAccountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, submittedAccountRequest.getInstitute(),
        };

        String invalidName = "%Invalid |Name";
        String invalidInstitute =
                StringHelperExtension.generateStringOfLength(FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);
        String invalidEmail = "";

        req = buildUpdateRequest(invalidName, invalidInstitute, invalidEmail);
        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(req, params);

        String expectedExceptionMessage = getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidName,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR)
                + System.lineSeparator()
                + getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidInstitute,
                FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.INSTITUTE_NAME_MAX_LENGTH)
                + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE_EMPTY_STRING,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.EMAIL_MAX_LENGTH);
        assertEquals(expectedExceptionMessage, ihrbe.getMessage());

        verifyNoTasksAdded();

        ______TS("body fields cannot be null");

        ihrbe = verifyHttpRequestBodyFailure(buildUpdateRequest(null, newInstitute, newEmail), params);
        assertEquals("name cannot be null", ihrbe.getMessage());

        ihrbe = verifyHttpRequestBodyFailure(buildUpdateRequest(newName, null, newEmail), params);
        assertEquals("institute cannot be null", ihrbe.getMessage());

        ihrbe = verifyHttpRequestBodyFailure(buildUpdateRequest(newName, newInstitute, null), params);
        assertEquals("email cannot be null", ihrbe.getMessage());

        ______TS("failure: null parameters");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, submittedAccountRequest.getInstitute(),
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_EMAIL),
                ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, submittedAccountRequest.getEmail(),
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

    private AccountRequestUpdateRequest buildUpdateRequest(String name, String institute, String email) {
        AccountRequestUpdateRequest req = new AccountRequestUpdateRequest();

        req.setInstructorName(name);
        req.setInstructorInstitute(institute);
        req.setInstructorEmail(email);

        return req;
    }

}
