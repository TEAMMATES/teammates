package teammates.it.ui.webapi;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestRejectionRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.RejectAccountRequestAction;

/**
 * SUT: {@link RejectAccountRequestAction}.
 */
public class RejectAccountRequestActionIT extends BaseActionIT<RejectAccountRequestAction> {

    private static final String TYPICAL_TITLE = "We are Unable to Create an Account for you";
    private static final String TYPICAL_BODY = new StringBuilder()
            .append("<p>Hi, Example</p>\n")
            .append("<p>Thanks for your interest in using TEAMMATES. ")
            .append("We are unable to create a TEAMMATES instructor account for you.</p>\n\n")
            .append("<p>\n")
            .append("  <strong>Reason:</strong> The email address you provided ")
            .append("is not an 'official' email address provided by your institution.<br />\n")
            .append("  <strong>Remedy:</strong> ")
            .append("Please re-submit an account request with your 'official' institution email address.\n")
            .append("</p>\n\n")
            .append("<p>If you need further clarification or would like to appeal this decision, ")
            .append("please feel free to contact us at teammates@comp.nus.edu.sg.</p>\n")
            .append("<p>Regards,<br />TEAMMATES Team.</p>\n")
            .toString();

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        // no need to call super.setUp() because the action handles its own transactions
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST_REJECTION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    public void testExecute() throws Exception {
        // See individual test methods below
    }

    @Test
    protected void testExecute_withReasonTitleAndBody_shouldRejectWithEmail()
            throws InvalidOperationException, InvalidHttpRequestBodyException, InvalidParametersException {
        AccountRequest bundleAccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest accountRequest = logic.createAccountRequestWithTransaction(bundleAccountRequest.getName(),
                bundleAccountRequest.getEmail(), bundleAccountRequest.getInstitute(),
                AccountRequestStatus.PENDING, bundleAccountRequest.getComments());
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(TYPICAL_TITLE, TYPICAL_BODY);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        RejectAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = action.execute();

        assertEquals(200, result.getStatusCode());

        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(AccountRequestStatus.REJECTED, data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());

        verifyNumberOfEmailsSent(1);
        EmailWrapper sentEmail = mockEmailSender.getEmailsSent().get(0);
        assertEquals(EmailType.ACCOUNT_REQUEST_REJECTION, sentEmail.getType());
        assertEquals(Config.SUPPORT_EMAIL, sentEmail.getBcc());
        assertEquals(accountRequest.getEmail(), sentEmail.getRecipient());
        assertEquals(SanitizationHelper.sanitizeForRichText(TYPICAL_BODY), sentEmail.getContent());
        assertEquals("TEAMMATES: " + TYPICAL_TITLE, sentEmail.getSubject());
    }

    @Test
    protected void testExecute_withoutReasonTitleAndBody_shouldRejectWithoutEmail()
            throws InvalidOperationException, InvalidHttpRequestBodyException, InvalidParametersException {
        AccountRequest bundleAccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest accountRequest = logic.createAccountRequestWithTransaction(bundleAccountRequest.getName(),
                bundleAccountRequest.getEmail(), bundleAccountRequest.getInstitute(),
                AccountRequestStatus.PENDING, bundleAccountRequest.getComments());
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        RejectAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = action.execute();

        assertEquals(200, result.getStatusCode());

        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(AccountRequestStatus.REJECTED, data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());

        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_withReasonBodyButNoTitle_shouldThrow() throws InvalidParametersException {
        AccountRequest bundleAccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest accountRequest = logic.createAccountRequestWithTransaction(bundleAccountRequest.getName(),
                bundleAccountRequest.getEmail(), bundleAccountRequest.getInstitute(),
                bundleAccountRequest.getStatus(), bundleAccountRequest.getComments());
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, TYPICAL_BODY);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("Both reason body and title need to be null to reject silently", ihrbe.getMessage());
        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_withReasonTitleButNoBody_shouldThrow() throws InvalidParametersException {
        AccountRequest bundleAccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest accountRequest = logic.createAccountRequestWithTransaction(bundleAccountRequest.getName(),
                bundleAccountRequest.getEmail(), bundleAccountRequest.getInstitute(),
                bundleAccountRequest.getStatus(), bundleAccountRequest.getComments());
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(TYPICAL_TITLE, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("Both reason body and title need to be null to reject silently", ihrbe.getMessage());
        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_alreadyRejected_shouldNotSendEmail()
            throws InvalidOperationException, InvalidHttpRequestBodyException, InvalidParametersException {
        AccountRequest bundleAccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        AccountRequest accountRequest = logic.createAccountRequestWithTransaction(bundleAccountRequest.getName(),
                bundleAccountRequest.getEmail(), bundleAccountRequest.getInstitute(),
                AccountRequestStatus.REJECTED, bundleAccountRequest.getComments());
        UUID id = accountRequest.getId();

        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(TYPICAL_TITLE, TYPICAL_BODY);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, id.toString()};

        RejectAccountRequestAction action = getAction(requestBody, params);
        JsonResult result = action.execute();

        assertEquals(result.getStatusCode(), 200);

        AccountRequestData data = (AccountRequestData) result.getOutput();
        assertEquals(accountRequest.getName(), data.getName());
        assertEquals(accountRequest.getEmail(), data.getEmail());
        assertEquals(accountRequest.getInstitute(), data.getInstitute());
        assertEquals(accountRequest.getStatus(), data.getStatus());
        assertEquals(accountRequest.getComments(), data.getComments());

        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_invalidUuid_shouldThrow() throws InvalidParametersException {
        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, "invalid"};

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(requestBody, params);
        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());
        verifyNoEmailsSent();
    }

    @Test
    protected void testExecute_accountRequestNotFound_shouldThrow() {
        AccountRequestRejectionRequest requestBody = new AccountRequestRejectionRequest(null, null);
        String uuid = UUID.randomUUID().toString();
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_REQUEST_ID, uuid};

        EntityNotFoundException enfe = verifyEntityNotFound(requestBody, params);
        assertEquals(String.format("Account request with id = %s not found", uuid), enfe.getMessage());
        verifyNoEmailsSent();
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
        HibernateUtil.commitTransaction();
    }
}
