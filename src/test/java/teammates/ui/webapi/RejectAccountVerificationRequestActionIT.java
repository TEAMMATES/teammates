package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountVerificationRequestRejectionRequest;

/**
 * SUT: {@link RejectAccountVerificationRequestAction}.
 */
public class RejectAccountVerificationRequestActionIT extends BaseActionIT<RejectAccountVerificationRequestAction> {
    private static final String TYPICAL_TITLE = "We are Unable to Create an Account for you";
    private static final String TYPICAL_BODY = new StringBuilder()
            .append("<p>Hi, Example</p>\n")
            .append("<p>Thanks for your interest in using TEAMMATES. ")
            .append("We are unable to create a TEAMMATES instructor account for you.</p>\n\n")
            .append("<p>\n")
            .append("  <strong>Reason:</strong> The email address you provided ")
            .append("is not an 'official' email address provided by your institution.<br />\n")
            .append("  <strong>Remedy:</strong> ")
            .append("Please re-submit an account verification request with your 'official' institution email address.\n")
            .append("</p>\n\n")
            .append("<p>If you need further clarification or would like to appeal this decision, ")
            .append("please feel free to contact us at teammates@comp.nus.edu.sg.</p>\n")
            .append("<p>Regards,<br />TEAMMATES Team.</p>\n")
            .toString();

    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_VERIFICATION_REQUEST_REJECTION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    public void testExecute() throws Exception {
        // See individual test methods below
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_withReasonTitleAndBody_shouldRejectWithEmail() {
        AccountVerificationRequest bundleAccountVerificationRequest =
                typicalBundle.accountVerificationRequests.get("unregisteredInstructor1");
        UUID accountId = typicalBundle.accounts.get("unregisteredInstructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                bundleAccountVerificationRequest.getName(),
                bundleAccountVerificationRequest.getEmail(), bundleAccountVerificationRequest.getInstitute().getName(),
                bundleAccountVerificationRequest.getInstitute().getCountry(),
                AccountVerificationRequestStatus.PENDING, bundleAccountVerificationRequest.getComments(), accountId));
        UUID id = accountVerificationRequest.getId();

        AccountVerificationRequestRejectionRequest requestBody =
                new AccountVerificationRequestRejectionRequest(TYPICAL_TITLE, TYPICAL_BODY);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        RejectAccountVerificationRequestAction action = getAction(requestBody, params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());

        AccountVerificationRequestData data = (AccountVerificationRequestData) result.getOutput();
        assertEquals(accountVerificationRequest.getName(), data.getName());
        assertEquals(accountVerificationRequest.getEmail(), data.getEmail());
        assertEquals(accountVerificationRequest.getInstitute().getName(), data.getInstitute());
        assertEquals(AccountVerificationRequestStatus.REJECTED, data.getStatus());
        assertEquals(accountVerificationRequest.getComments(), data.getComments());

        verifyNumberOfEmailsQueued(1);
        EmailWrapper sentEmail = getQueuedEmails().get(0);
        assertEquals(EmailType.ACCOUNT_VERIFICATION_REQUEST_REJECTION, sentEmail.getType());
        assertEquals(Config.SUPPORT_EMAIL, sentEmail.getBcc());
        assertEquals(accountVerificationRequest.getEmail(), sentEmail.getRecipient());
        assertEquals(SanitizationHelper.sanitizeForRichText(TYPICAL_BODY), sentEmail.getContent());
        assertEquals("TEAMMATES: " + TYPICAL_TITLE, sentEmail.getSubject());
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_withoutReasonTitleAndBody_shouldRejectWithoutEmail() {
        AccountVerificationRequest bundleAccountVerificationRequest =
                typicalBundle.accountVerificationRequests.get("unregisteredInstructor1");
        UUID accountId = typicalBundle.accounts.get("unregisteredInstructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                bundleAccountVerificationRequest.getName(),
                bundleAccountVerificationRequest.getEmail(), bundleAccountVerificationRequest.getInstitute().getName(),
                bundleAccountVerificationRequest.getInstitute().getCountry(),
                AccountVerificationRequestStatus.PENDING, bundleAccountVerificationRequest.getComments(), accountId));
        UUID id = accountVerificationRequest.getId();

        AccountVerificationRequestRejectionRequest requestBody = new AccountVerificationRequestRejectionRequest(null, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        RejectAccountVerificationRequestAction action = getAction(requestBody, params);
        JsonResult result = getJsonResult(action);

        assertEquals(200, result.getStatusCode());

        AccountVerificationRequestData data = (AccountVerificationRequestData) result.getOutput();
        assertEquals(accountVerificationRequest.getName(), data.getName());
        assertEquals(accountVerificationRequest.getEmail(), data.getEmail());
        assertEquals(accountVerificationRequest.getInstitute().getName(), data.getInstitute());
        assertEquals(AccountVerificationRequestStatus.REJECTED, data.getStatus());
        assertEquals(accountVerificationRequest.getComments(), data.getComments());

        verifyNoEmailsQueued();
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_withReasonBodyButNoTitle_shouldThrow() {
        AccountVerificationRequest bundleAccountVerificationRequest =
                typicalBundle.accountVerificationRequests.get("unregisteredInstructor1");
        UUID accountId = typicalBundle.accounts.get("unregisteredInstructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                bundleAccountVerificationRequest.getName(),
                bundleAccountVerificationRequest.getEmail(), bundleAccountVerificationRequest.getInstitute().getName(),
                bundleAccountVerificationRequest.getInstitute().getCountry(),
                AccountVerificationRequestStatus.PENDING, bundleAccountVerificationRequest.getComments(), accountId));
        UUID id = accountVerificationRequest.getId();

        AccountVerificationRequestRejectionRequest requestBody =
                new AccountVerificationRequestRejectionRequest(null, TYPICAL_BODY);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("Both reason body and title need to be null to reject silently", ihrbe.getMessage());
        verifyNoEmailsQueued();
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_withReasonTitleButNoBody_shouldThrow() {
        AccountVerificationRequest bundleAccountVerificationRequest =
                typicalBundle.accountVerificationRequests.get("unregisteredInstructor1");
        UUID accountId = typicalBundle.accounts.get("unregisteredInstructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                bundleAccountVerificationRequest.getName(),
                bundleAccountVerificationRequest.getEmail(), bundleAccountVerificationRequest.getInstitute().getName(),
                bundleAccountVerificationRequest.getInstitute().getCountry(),
                AccountVerificationRequestStatus.PENDING, bundleAccountVerificationRequest.getComments(), accountId));
        UUID id = accountVerificationRequest.getId();

        AccountVerificationRequestRejectionRequest requestBody =
                new AccountVerificationRequestRejectionRequest(TYPICAL_TITLE, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, params);

        assertEquals("Both reason body and title need to be null to reject silently", ihrbe.getMessage());
        verifyNoEmailsQueued();
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_alreadyRejected_shouldThrow() {
        AccountVerificationRequest bundleAccountVerificationRequest =
                typicalBundle.accountVerificationRequests.get("unregisteredInstructor1");
        UUID accountId = typicalBundle.accounts.get("unregisteredInstructor1").getId();
        AccountVerificationRequest accountVerificationRequest = inTransaction(() -> logic.createAccountVerificationRequest(
                bundleAccountVerificationRequest.getName(),
                bundleAccountVerificationRequest.getEmail(), bundleAccountVerificationRequest.getInstitute().getName(),
                bundleAccountVerificationRequest.getInstitute().getCountry(),
                AccountVerificationRequestStatus.REJECTED, bundleAccountVerificationRequest.getComments(), accountId));
        UUID id = accountVerificationRequest.getId();
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, id.toString()};

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Account verification request with id " + id
                + " is not in pending state and cannot be rejected.", ioe.getMessage());

        verifyNoEmailsSent();
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_invalidUuid_shouldThrow() {
        AccountVerificationRequestRejectionRequest requestBody = new AccountVerificationRequestRejectionRequest(null, null);
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, "invalid"};

        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(requestBody, params);
        assertEquals("Expected UUID value for id parameter, but found: [invalid]", ihpe.getMessage());
        verifyNoEmailsSent();
    }

    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute_accountVerificationRequestNotFound_shouldThrow() {
        AccountVerificationRequestRejectionRequest requestBody = new AccountVerificationRequestRejectionRequest(null, null);
        String uuid = UUID.randomUUID().toString();
        String[] params = new String[] {Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, uuid};

        EntityNotFoundException enfe = verifyEntityNotFound(requestBody, params);
        assertEquals(String.format("Account verification request with id = %s not found", uuid), enfe.getMessage());
        verifyNoEmailsSent();
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
