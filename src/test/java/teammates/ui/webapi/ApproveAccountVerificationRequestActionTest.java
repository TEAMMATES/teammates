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
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link ApproveAccountVerificationRequestAction}.
 */
public class ApproveAccountVerificationRequestActionTest
        extends BaseActionTest<ApproveAccountVerificationRequestAction, AccountVerificationRequestData> {

    @Test(groups = GroupNames.ACTION)
    public void approveAccountVerificationRequestAction_pendingRequest_approvesAndQueuesEmail() {
        var account = given.account("request-owner");
        var institute = given.institute("institute", i -> i.name("Northbridge Institute of Technology").country("SG"));
        var requestRef = given.accountVerificationRequest("request", ar -> ar
                .account(account.alias())
                .institute(institute.alias())
                .name("Dr Elena Hart")
                .email("elena.hart@northbridge.edu")
                .pending()
                .comments("Please verify my account."));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, requestRef.id().toString());

        AccountVerificationRequestData result = execute(request);

        assertEquals(AccountVerificationRequestStatus.APPROVED, result.getStatus());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.ACCOUNT_VERIFICATION_APPROVED, queuedEmails.get(0).getType());
        assertEquals("elena.hart@northbridge.edu", queuedEmails.get(0).getRecipient());
    }

    @Test(groups = GroupNames.ACTION)
    public void approveAccountVerificationRequestAction_approvedRequest_throwsInvalidOperationException() {
        var account = given.account("request-owner");
        var requestRef = given.accountVerificationRequest("request", ar -> ar
                .account(account.alias())
                .approved());
        persistGivenData(given);

        InvalidOperationException exception = assertActionThrows(
                InvalidOperationException.class,
                new RequestContext()
                        .withAdminAuth()
                        .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, requestRef.id().toString()));

        assertEquals("Account verification request with id " + requestRef.id() + " is already approved.",
                exception.getMessage());
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
