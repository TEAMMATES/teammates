package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
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
    protected void setUp() {
        // CreateAccountRequestAction handles its own transactions;
        // There is thus no need to setup a transaction.
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
        assertEquals("email cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullName_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("name cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullInstitute_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("institute cannot be null", ihrbException.getMessage());
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
        assertEquals(expectedMessage, ihrbException.getMessage());
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
        assertEquals(expectedMessage, ihrbException.getMessage());
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
        assertEquals(expectedMessage, ihrbException.getMessage());
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
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertEquals("My road leads into the desert. I can see it.", output.getComments());
        assertNull(output.getRegisteredAt());
        HibernateUtil.beginTransaction();
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        HibernateUtil.commitTransaction();
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
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
        assertEquals("kwisatz.haderach@atreides.org", output.getEmail());
        assertEquals("Paul Atreides", output.getName());
        assertEquals("House Atreides", output.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, output.getStatus());
        assertNull(output.getComments());
        assertNull(output.getRegisteredAt());
        HibernateUtil.beginTransaction();
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        HibernateUtil.commitTransaction();
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertNull(accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Test
    void testExecute_accountRequestWithSameEmailAddressAndInstituteAlreadyExists_createsSuccessfully()
            throws InvalidParametersException {
        HibernateUtil.beginTransaction();
        AccountRequest existingAccountRequest = logic.createAccountRequest("Paul Atreides",
                "kwisatz.haderach@atreides.org",
                "House Atreides", AccountRequestStatus.PENDING, "My road leads into the desert. I can see it.");
        HibernateUtil.commitTransaction();
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
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
        HibernateUtil.beginTransaction();
        AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(output.getRegistrationKey());
        HibernateUtil.commitTransaction();
        assertEquals("kwisatz.haderach@atreides.org", accountRequest.getEmail());
        assertEquals("Paul Atreides", accountRequest.getName());
        assertEquals("House Atreides", accountRequest.getInstitute());
        assertEquals(AccountRequestStatus.PENDING, accountRequest.getStatus());
        assertEquals("My road leads into the desert. I can see it.", accountRequest.getComments());
        assertNull(accountRequest.getRegisteredAt());
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);
        verifyNumberOfEmailsSent(2);
        EmailWrapper sentAdminAlertEmail = mockEmailSender.getEmailsSent().get(0);
        EmailWrapper sentAcknowledgementEmail = mockEmailSender.getEmailsSent().get(1);
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ADMIN_ALERT, sentAdminAlertEmail.getType());
        assertEquals(EmailType.NEW_ACCOUNT_REQUEST_ACKNOWLEDGEMENT, sentAcknowledgementEmail.getType());
    }

    @Test
    void testExecute_typicalCaseAsAdmin_noEmailsSent() {
        loginAsAdminWithTransaction();
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        request.setInstructorComments("My road leads into the desert. I can see it.");
        CreateAccountRequestAction action = getAction(request);
        JsonResult result = getJsonResult(action);
        AccountRequestData output = (AccountRequestData) result.getOutput();
        assertNull(output.getRegisteredAt());
        verifyNoEmailsSent();
        logoutUser();
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAccessibleWithoutLogin();
    }

    @Override
    @AfterMethod
    protected void tearDown() {
        HibernateUtil.beginTransaction();
        List<AccountRequest> accountRequests = logic.getPendingAccountRequests();
        for (AccountRequest ar : accountRequests) {
            logic.deleteAccountRequest(ar.getId());
        }
        accountRequests = logic.getPendingAccountRequests();
        HibernateUtil.commitTransaction();
        assert accountRequests.isEmpty();
    }
}
