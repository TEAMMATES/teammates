package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.ui.output.AccountRequestCreateErrorResults;
import teammates.ui.output.JoinLinkData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.AccountRequestCreateIntent;
import teammates.ui.request.AccountRequestCreateRequest;
import teammates.ui.request.AccountRequestType;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionTest extends BaseActionTest<CreateAccountRequestAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    protected void testExecute_publicCreate() throws Exception {
        ______TS("typical success case");

        String name = "James Bond";
        String institute = "TEAMMATES Test Institute, United Kingdom";
        String email = "jamesbond89@tmt.tmt";
        String homePageUrl = "https://nus-oss.github.io/";
        String comments = "Is TEAMMATES free to use?";

        String nameWithSpaces = "   James     Bond  ";
        String pureInstituteWithSpaces = "   TEAMMATES  Test    Institute   ";
        String pureCountryWithSpaces = "   United   Kingdom   ";
        String emailWithSpaces = "   " + email + "   ";
        String homePageUrlWithSpaces = "   " + homePageUrl + "   ";
        String commentsWithSpaces = "   " + comments + "   ";

        AccountRequestCreateRequest req = buildCreateRequest(nameWithSpaces, pureInstituteWithSpaces, pureCountryWithSpaces,
                emailWithSpaces, homePageUrlWithSpaces, commentsWithSpaces);
        String[] params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.PUBLIC_CREATE.toString(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "",
                Const.ParamsNames.ACCOUNT_REQUEST_TYPE, AccountRequestType.INSTRUCTOR_ACCOUNT.toString(),
        };
        CreateAccountRequestAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);

        AccountRequestAttributes actualAccountRequest = logic.getAccountRequest(email, institute);

        assertEquals(name, actualAccountRequest.getName());
        assertEquals(institute, actualAccountRequest.getInstitute());
        assertEquals(email, actualAccountRequest.getEmail());
        assertEquals(homePageUrl, actualAccountRequest.getHomePageUrl());
        assertEquals(comments, actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.SUBMITTED, actualAccountRequest.getStatus());
        assertNotNull(actualAccountRequest.getCreatedAt());
        assertNull(actualAccountRequest.getLastProcessedAt());
        assertNull(actualAccountRequest.getRegisteredAt());
        assertNotNull(actualAccountRequest.getRegistrationKey());

        MessageOutput messageOutput = (MessageOutput) result.getOutput();
        assertEquals("Account request successfully created.", messageOutput.getMessage());

        verifyNoEmailsSent();
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("failure: account request already exists");

        verifyInvalidOperation(req, params);

        ______TS("failure: invalid body fields");

        String invalidName = "James%20Bond99";
        String invalidPureInstitute = " ";
        String invalidPureCountry = ",Singapore";
        String invalidEmail = "invalid_email@tmt";
        String invalidHomePageUrl = StringHelperExtension
                .generateStringOfLength(FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH + 1);
        String invalidComments = StringHelperExtension
                .generateStringOfLength(FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH + 1);

        req = buildCreateRequest(invalidName, invalidPureInstitute, invalidPureCountry,
                invalidEmail, invalidHomePageUrl, invalidComments);
        action = getAction(req, params);
        result = getJsonResult(action, HttpStatus.SC_BAD_REQUEST);

        AccountRequestCreateErrorResults errorResults = (AccountRequestCreateErrorResults) result.getOutput();
        assertEquals(getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidName,
                        FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR),
                errorResults.getInvalidNameMessage());
        assertEquals(getPopulatedEmptyStringErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_FIELD_NAME,
                        FieldValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH),
                errorResults.getInvalidInstituteMessage());
        assertEquals(getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidPureCountry,
                        FieldValidator.ACCOUNT_REQUEST_COUNTRY_NAME_FIELD_NAME,
                        FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR),
                errorResults.getInvalidCountryMessage());
        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidEmail,
                        FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH),
                errorResults.getInvalidEmailMessage());
        assertEquals(getPopulatedErrorMessage(
                FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, invalidHomePageUrl,
                        FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH),
                errorResults.getInvalidHomePageUrlMessage());
        assertEquals(getPopulatedErrorMessage(
                        FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, invalidComments,
                        FieldValidator.ACCOUNT_REQUEST_COMMENTS_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                        FieldValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH),
                errorResults.getInvalidCommentsMessage());

        ______TS("failure: null parameters");

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.PUBLIC_CREATE.toString(),
                Const.ParamsNames.ACCOUNT_REQUEST_TYPE, AccountRequestType.INSTRUCTOR_ACCOUNT.toString(),
        };
        req = buildCreateRequest(name, pureInstituteWithSpaces, pureCountryWithSpaces, email, homePageUrl, comments);

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(req, params);
        assertEquals("Please check the \"I'm not a robot\" box before submission.", ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.PUBLIC_CREATE.toString(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "",
        };

        ihpe = verifyHttpParameterFailure(req, params);
        assertEquals("Only instructor accounts can be created.", ihpe.getMessage());

        ______TS("failure: invalid parameters");

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.PUBLIC_CREATE.toString(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "",
                Const.ParamsNames.ACCOUNT_REQUEST_TYPE, AccountRequestType.STUDENT_ACCOUNT.toString(),
        };

        ihpe = verifyHttpParameterFailure(req, params);
        assertEquals("Only instructor accounts can be created.", ihpe.getMessage());
    }

    @Test
    protected void testExecute_adminCreate() throws Exception {
        ______TS("typical success case");

        String name = "Spider-Man";
        String institute = "TEAMMATES Test Institute, Singapore";
        String email = "spider_man@tmt.tmt";
        String homePageUrl = "";
        String comments = "";

        AccountRequestCreateRequest req = buildCreateRequest(name, institute, "", email, homePageUrl, comments);
        String[] params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.ADMIN_CREATE.toString(),
                Const.ParamsNames.ACCOUNT_REQUEST_TYPE, AccountRequestType.INSTRUCTOR_ACCOUNT.toString(),
        };
        CreateAccountRequestAction action = getAction(req, params);
        JsonResult result = getJsonResult(action);

        AccountRequestAttributes actualAccountRequest = logic.getAccountRequest(email, institute);

        assertEquals(name, actualAccountRequest.getName());
        assertEquals(institute, actualAccountRequest.getInstitute());
        assertEquals(email, actualAccountRequest.getEmail());
        assertEquals(homePageUrl, actualAccountRequest.getHomePageUrl());
        assertEquals(comments, actualAccountRequest.getComments());
        assertEquals(AccountRequestStatus.APPROVED, actualAccountRequest.getStatus());
        assertNotNull(actualAccountRequest.getCreatedAt());
        assertNotNull(actualAccountRequest.getLastProcessedAt());
        assertEquals(actualAccountRequest.getCreatedAt(), actualAccountRequest.getLastProcessedAt());
        assertNull(actualAccountRequest.getRegisteredAt());
        assertNotNull(actualAccountRequest.getRegistrationKey());

        String joinLink = actualAccountRequest.getRegistrationUrl();
        JoinLinkData output = (JoinLinkData) result.getOutput();
        assertEquals(joinLink, output.getJoinLink());

        verifyNumberOfEmailsSent(1);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), name), emailSent.getSubject());
        assertEquals(email, emailSent.getRecipient());
        assertTrue(emailSent.getContent().contains(joinLink));

        ______TS("failure: account request already exists");

        actualAccountRequest = typicalBundle.accountRequests.get("submittedRequest4");

        req = buildCreateRequest("Arbitrary Name", actualAccountRequest.getInstitute(), "",
                actualAccountRequest.getEmail(), "Arbitrary URL", "Arbitrary Comments");

        InvalidOperationException ioe = verifyInvalidOperation(req, params);
        assertEquals("An account request already exists with status " + actualAccountRequest.getStatus() + ".",
                ioe.getMessage());

        verifyNoEmailsSent();
        verifyNoTasksAdded();

        ______TS("failure: invalid body fields");

        String invalidInstitute = "TEAMMATES Test Institute%Singapore";

        req = buildCreateRequest(name, invalidInstitute, "", email, homePageUrl, comments);

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(req, params);
        assertEquals(getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, invalidInstitute,
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME, FieldValidator.REASON_CONTAINS_INVALID_CHAR),
                ihrbe.getMessage());

        verifyNoEmailsSent();
        verifyNoTasksAdded();

        ______TS("failure: null parameters");

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.ADMIN_CREATE.toString(),
        };

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(req, params);
        assertEquals("Only instructor accounts can be created.", ihpe.getMessage());

        ______TS("failure: invalid parameters");

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.ADMIN_CREATE.toString(),
                Const.ParamsNames.ACCOUNT_REQUEST_TYPE, AccountRequestType.STUDENT_ACCOUNT.toString(),
        };

        ihpe = verifyHttpParameterFailure(req, params);
        assertEquals("Only instructor accounts can be created.", ihpe.getMessage());
    }

    @Test
    protected void testExecute_nullBodyFields_throwException() {
        ______TS("body fields cannot be null");

        String name = "James Bond";
        String email = "jamesbond89@tmt.tmt";
        String pureInstitute = "TEAMMATES Test Institute";
        String pureCountry = "United Kingdom";
        String homePageUrl = "https://nus-oss.github.io/";
        String comments = "Is TEAMMATES free to use?";

        String[] params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.PUBLIC_CREATE.toString(),
                Const.ParamsNames.USER_CAPTCHA_RESPONSE, "",
                Const.ParamsNames.ACCOUNT_REQUEST_TYPE, AccountRequestType.INSTRUCTOR_ACCOUNT.toString(),
        };

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(
                buildCreateRequest(null, pureInstitute, pureCountry, email, homePageUrl, comments), params);
        assertEquals("name cannot be null", ihrbe.getMessage());

        ihrbe = verifyHttpRequestBodyFailure(
                buildCreateRequest(name, null, pureCountry, email, homePageUrl, comments), params);
        assertEquals("institute cannot be null", ihrbe.getMessage());

        ihrbe = verifyHttpRequestBodyFailure(
                buildCreateRequest(name, pureInstitute, null, email, homePageUrl, comments), params);
        assertEquals("country cannot be null", ihrbe.getMessage());

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.ADMIN_CREATE.toString(),
                Const.ParamsNames.ACCOUNT_REQUEST_TYPE, AccountRequestType.INSTRUCTOR_ACCOUNT.toString(),
        };

        ihrbe = verifyHttpRequestBodyFailure(
                buildCreateRequest(name, pureInstitute, pureCountry, null, homePageUrl, comments), params);
        assertEquals("email cannot be null", ihrbe.getMessage());

        ihrbe = verifyHttpRequestBodyFailure(
                buildCreateRequest(name, pureInstitute, pureCountry, email, null, comments), params);
        assertEquals("home page url cannot be null", ihrbe.getMessage());

        ihrbe = verifyHttpRequestBodyFailure(
                buildCreateRequest(name, pureInstitute, pureCountry, email, homePageUrl, null), params);
        assertEquals("comments cannot be null", ihrbe.getMessage());
    }

    @Override
    @Test
    protected void testExecute() {
        // see individual tests
    }

    @Override
    @Test
    protected void testAccessControl() {
        ______TS("only admin can access for intent ADMIN_CREATE");

        String[] params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.ADMIN_CREATE.toString(),
        };

        verifyOnlyAdminCanAccess(params);

        ______TS("any user can access for intent PUBLIC_CREATE");

        params = new String[] {
                Const.ParamsNames.INTENT, AccountRequestCreateIntent.PUBLIC_CREATE.toString(),
        };

        verifyAnyUserCanAccess(params);

        ______TS("intent cannot be null");

        InvalidHttpParameterException ihpe = verifyHttpParameterFailureAcl();
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INTENT), ihpe.getMessage());
    }

    @Test
    public void testGenerateInstitute() {
        assertEquals("TMT, Singapore", CreateAccountRequestAction.generateInstitute("TMT", "Singapore"));
        assertThrows(AssertionError.class, () -> CreateAccountRequestAction.generateInstitute(null, "Singapore"));
        assertThrows(AssertionError.class, () -> CreateAccountRequestAction.generateInstitute("TMT", null));
    }

    private AccountRequestCreateRequest buildCreateRequest(String name, String institute, String country, String email,
                                                           String url, String comments) {
        AccountRequestCreateRequest req = new AccountRequestCreateRequest();

        req.setInstructorName(name);
        req.setInstructorInstitute(institute);
        req.setInstructorCountry(country);
        req.setInstructorEmail(email);
        req.setInstructorHomePageUrl(url);
        req.setComments(comments);

        return req;
    }

}
