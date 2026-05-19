package teammates.it.ui.webapi;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.CreateAccountRequestAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionIT extends BaseActionIT<CreateAccountRequestAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void testExecute() throws Exception {
        // This is separated into different test methods.
    }

    @Test
    void testExecute_nullEmail_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        Assertions.assertEquals("email cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullName_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        Assertions.assertEquals("name cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullInstitute_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        Assertions.assertEquals("institute cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_invalidEmail_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("invalid email address");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        String expectedMessage = "\"invalid email address\" is not acceptable to TEAMMATES as a/an email because it is not "
                + "in the correct format. An email address contains some text followed by one '@' sign followed by some "
                + "more text, and should end with a top level domain address like .com. It cannot be longer than 254 "
                + "characters, cannot be empty and cannot contain spaces.";
        Assertions.assertEquals(expectedMessage, ihrbException.getMessage());
    }

    @Test
    void testExecute_invalidName_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Pau| Atreides");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        String expectedMessage = "\"Pau| Atreides\" is not acceptable to TEAMMATES as a/an person name because it contains "
                + "invalid characters. A/An person name must start with an alphanumeric character, and cannot contain any "
                + "vertical bar (|) or percent sign (%).";
        Assertions.assertEquals(expectedMessage, ihrbException.getMessage());
    }

    @Test
    void testExecute_invalidInstitute_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreide%");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        String expectedMessage = "\"House Atreide%\" is not acceptable to TEAMMATES as a/an institute name because it "
                + "contains invalid characters. A/An institute name must start with an alphanumeric character, and cannot "
                + "contain any vertical bar (|) or percent sign (%).";
        Assertions.assertEquals(expectedMessage, ihrbException.getMessage());
    }

    @Test
    void testExecute_typicalCase_createsSuccessfully() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        Assertions.assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        Assertions.assertEquals("Paul Atreides", output.getName());
        Assertions.assertEquals("House Atreides", output.getInstitute());
        Assertions.assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        Assertions.assertEquals("My road leads into the desert. I can see it.", output.getComments());
        Assertions.assertNull(output.getRegisteredAt());
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        Assertions.assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        Assertions.assertEquals("Paul Atreides", accountRequest.getName());
        Assertions.assertEquals("House Atreides", accountRequest.getInstitute());
        Assertions.assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        Assertions.assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        Assertions.assertNull(accountRequest.getRegisteredAt());
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        Assertions.assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        Assertions.assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Test
    void testExecute_leadingAndTrailingSpacesAndNullComments_createsSuccessfully() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail(" kwisatz.haderach@atreides.org   ");
        request.setInstructorName("  Paul Atreides ");
        request.setInstructorInstitution("   House Atreides  ");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        Assertions.assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        Assertions.assertEquals("Paul Atreides", output.getName());
        Assertions.assertEquals("House Atreides", output.getInstitute());
        Assertions.assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        Assertions.assertNull(output.getComments());
        Assertions.assertNull(output.getRegisteredAt());
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        Assertions.assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        Assertions.assertEquals("Paul Atreides", accountRequest.getName());
        Assertions.assertEquals("House Atreides", accountRequest.getInstitute());
        Assertions.assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        Assertions.assertNull(accountRequest.getComments());
        Assertions.assertNull(accountRequest.getRegisteredAt());
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        Assertions.assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        Assertions.assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Test
    void testExecute_accountRequestWithSameEmailAddressAndInstituteAlreadyExists_createsSuccessfully()
            throws InvalidParametersException {
        AccountRequest existingAccountRequest = logic.createAccountRequest("Paul Atreides",
                "kwisatz.haderach@atreides.org",
                "House Atreides", AccountRequestStatus.PENDING, "My road leads into the desert. I can see it.");
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        Assertions.assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        Assertions.assertEquals("Paul Atreides", output.getName());
        Assertions.assertEquals("House Atreides", output.getInstitute());
        Assertions.assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        Assertions.assertEquals("My road leads into the desert. I can see it.", output.getComments());
        Assertions.assertNull(output.getRegisteredAt());
        Assertions.assertNotEquals(output.getRegistrationKey(), existingAccountRequest.getRegistrationKey());
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        Assertions.assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        Assertions.assertEquals("Paul Atreides", accountRequest.getName());
        Assertions.assertEquals("House Atreides", accountRequest.getInstitute());
        Assertions.assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        Assertions.assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        Assertions.assertNull(accountRequest.getRegisteredAt());
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        Assertions.assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        Assertions.assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Test
    void testExecute_typicalCaseAsAdmin_noEmailsSent() {
        loginAsAdmin();
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        Assertions.assertNull(output.getRegisteredAt());
        verifyNoEmailsSent();
        logoutUser();
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAccessibleWithoutLogin();
    }
}
