package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.AccountRequest;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionIT extends BaseActionIT<CreateAccountRequestAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        loginAsInstructor(typicalBundle.accounts.get("instructor1").getGoogleId());
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() throws Exception {
        // This is separated into different test methods.
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_nullEmail_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorCountry("SG");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("email cannot be null", ihrbException.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_nullName_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorCountry("SG");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("name cannot be null", ihrbException.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_nullInstitute_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("institute cannot be null", ihrbException.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_invalidEmail_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("invalid email address");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorCountry("SG");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        String expectedMessage = "\"invalid email address\" is not acceptable to TEAMMATES as a/an email because it is not "
                + "in the correct format. An email address contains some text followed by one '@' sign followed by some "
                + "more text, and should end with a top level domain address like .com. It cannot be longer than 254 "
                + "characters, cannot be empty and cannot contain spaces.";
        assertEquals(expectedMessage, ihrbException.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_invalidName_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Pau| Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorCountry("SG");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        String expectedMessage = "\"Pau| Atreides\" is not acceptable to TEAMMATES as a/an person name because it contains "
                + "invalid characters. A/An person name must start with an alphanumeric character, and cannot contain any "
                + "vertical bar (|) or percent sign (%).";
        assertEquals(expectedMessage, ihrbException.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_invalidInstitute_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreide%");
        request.setInstructorCountry("SG");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        String expectedMessage = "\"House Atreide%\" is not acceptable to TEAMMATES as a/an institute name because it "
                + "contains invalid characters. A/An institute name must start with an alphanumeric character, and cannot "
                + "contain any vertical bar (|) or percent sign (%).";
        assertEquals(expectedMessage, ihrbException.getMessage());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_typicalCase_createsSuccessfully() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorCountry("SG");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertEquals("My road leads into the desert. I can see it.", output.getComments());
        assertNull(output.getRegisteredAt());
        AccountRequest accountRequest =
                inTransaction(() -> logic.getAccountRequestByRegistrationKey(output.getRegistrationKey()));
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute().getName());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_leadingAndTrailingSpacesAndNullComments_createsSuccessfully() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail(" kwisatz.haderach@atreides.org   ");
        request.setInstructorName("  Paul Atreides ");
        request.setInstructorInstitution("   House Atreides  ");
        request.setInstructorCountry("SG");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertNull(output.getComments());
        assertNull(output.getRegisteredAt());
        AccountRequest accountRequest =
                inTransaction(() -> logic.getAccountRequestByRegistrationKey(output.getRegistrationKey()));
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute().getName());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertNull(accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Test(groups = GroupNames.INTEGRATION)
    void testExecute_accountRequestWithSameEmailAddressAndInstituteAlreadyExists_createsSuccessfully() {
        UUID accountId = typicalBundle.accounts.get("instructor1").getId();
        AccountRequest existingAccountRequest = inTransaction(() -> logic.createAccountRequest("Paul Atreides",
                "kwisatz.haderach@atreides.org",
                "House Atreides", "SG", AccountRequestStatus.PENDING, "My road leads into the desert. I can see it.",
                accountId));
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorCountry("SG");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertEquals("My road leads into the desert. I can see it.", output.getComments());
        assertNull(output.getRegisteredAt());
        assertNotEquals(output.getRegistrationKey(), existingAccountRequest.getRegistrationKey());
        AccountRequest accountRequest =
                inTransaction(() -> logic.getAccountRequestByRegistrationKey(output.getRegistrationKey()));
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute().getName());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}
