package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestRejectionType;
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
    public void rejectAccountVerificationRequestAction_withRejectionType_rejectsAndQueuesEmail() {
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
                        AccountVerificationRequestRejectionType.NOT_OFFICIAL_EMAIL, null));

        AccountVerificationRequestData result = execute(request);

        assertEquals(AccountVerificationRequestStatus.REJECTED, result.getStatus());
        assertEquals(AccountVerificationRequestRejectionType.NOT_OFFICIAL_EMAIL, result.getRejectionType());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.ACCOUNT_VERIFICATION_REJECTED, queuedEmails.get(0).getType());
        assertEquals("maya.bennett@westhaven.edu", queuedEmails.get(0).getRecipient());
    }

    @Test(groups = GroupNames.ACTION)
    public void rejectAccountVerificationRequestAction_withOthersType_rejectsAndQueuesEmail() {
        var account = given.account("request-owner");
        var requestRef = given.accountVerificationRequest("request", ar -> ar
                .account(account.alias())
                .pending());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, requestRef.id().toString())
                .withRequest(new AccountVerificationRequestRejectionRequest(
                        AccountVerificationRequestRejectionType.OTHERS, null));

        AccountVerificationRequestData result = execute(request);

        assertEquals(AccountVerificationRequestStatus.REJECTED, result.getStatus());
        assertEquals(AccountVerificationRequestRejectionType.OTHERS, result.getRejectionType());
        assertEquals(1, getQueuedEmails().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void rejectAccountVerificationRequestAction_withNullRejectionType_throwsInvalidHttpRequestBodyException() {
        var account = given.account("request-owner");
        var requestRef = given.accountVerificationRequest("request", ar -> ar
                .account(account.alias())
                .pending());
        persistGivenData(given);

        assertActionThrows(
                InvalidHttpRequestBodyException.class,
                new RequestContext()
                        .withAdminAuth()
                        .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, requestRef.id().toString())
                        .withRequest(new AccountVerificationRequestRejectionRequest(null, null)));

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
