package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountVerificationRequestRejectionRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link RejectAccountVerificationRequestAction}.
 */
public class RejectAccountVerificationRequestActionTest
        extends BaseActionTest<RejectAccountVerificationRequestAction, AccountVerificationRequestData> {

    @Test(groups = GroupNames.ACTION)
    public void rejectAccountVerificationRequestAction_withReason_rejectsAndQueuesEmail() {
        var account = given.account("request-owner");
        var requestRef = given.accountVerificationRequest("request", ar -> ar
                .account(account.alias())
                .name("Dr Maya Bennett")
                .email("maya.bennett@westhaven.edu")
                .pending());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, requestRef.id().toString())
                .withRequest(new AccountVerificationRequestRejectionRequest(
                        "We are Unable to Approve Your Verification Request",
                        "<p>Please submit your request again with your official institution email address.</p>"));

        AccountVerificationRequestData result = execute(request);

        assertEquals(AccountVerificationRequestStatus.REJECTED, result.getStatus());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.ACCOUNT_VERIFICATION_REJECTED, queuedEmails.get(0).getType());
        assertEquals("maya.bennett@westhaven.edu", queuedEmails.get(0).getRecipient());
    }

    @Test(groups = GroupNames.ACTION)
    public void rejectAccountVerificationRequestAction_withoutReason_rejectsSilently() {
        var account = given.account("request-owner");
        var requestRef = given.accountVerificationRequest("request", ar -> ar
                .account(account.alias())
                .pending());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, requestRef.id().toString());

        AccountVerificationRequestData result = execute(request);

        assertEquals(AccountVerificationRequestStatus.REJECTED, result.getStatus());
        assertEquals(0, getQueuedEmails().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void rejectAccountVerificationRequestAction_reasonTitleWithoutBody_throwsInvalidHttpRequestBodyException() {
        var account = given.account("request-owner");
        var requestRef = given.accountVerificationRequest("request", ar -> ar
                .account(account.alias())
                .pending());
        persistGivenData(given);

        InvalidHttpRequestBodyException exception = assertActionThrows(
                InvalidHttpRequestBodyException.class,
                new RequestContext()
                        .withAdminAuth()
                        .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, requestRef.id().toString())
                        .withRequest(new AccountVerificationRequestRejectionRequest(
                                "We are Unable to Approve Your Verification Request", null)));

        assertEquals("Both reason body and title need to be null to reject silently", exception.getMessage());
        assertEquals(0, getQueuedEmails().size());
    }

    private List<EmailWrapper> getQueuedEmails() {
        return mockTaskQueuer.getTasksAdded().stream()
                .map(TaskWrapper::getRequestBody)
                .map(SendEmailRequest.class::cast)
                .map(SendEmailRequest::getEmail)
                .toList();
    }
}
