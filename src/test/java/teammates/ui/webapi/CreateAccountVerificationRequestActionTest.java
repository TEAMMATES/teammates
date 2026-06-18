package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.AccountVerificationRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link CreateAccountVerificationRequestAction}.
 */
public class CreateAccountVerificationRequestActionTest
        extends BaseActionTest<CreateAccountVerificationRequestAction, AccountVerificationRequestData> {

    @Test(groups = GroupNames.ACTION)
    public void createAccountVerificationRequestAction_validRequest_createsRequestAndQueuesEmails() {
        var account = given.account("requester-account");
        persistGivenData(given);

        AccountCreateRequest requestBody = new AccountCreateRequest();
        requestBody.setInstructorEmail("  amelia.hart@northbridge.edu ");
        requestBody.setInstructorName("  Dr Amelia Hart ");
        requestBody.setInstructorInstitution("  Northbridge Institute of Technology ");
        requestBody.setInstructorCountry("SG");
        requestBody.setInstructorComments("Please verify my instructor account for upcoming software design courses.");

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(requestBody);

        AccountVerificationRequestData result = execute(request);

        assertEquals("amelia.hart@northbridge.edu", result.getEmail());
        assertEquals("Dr Amelia Hart", result.getName());
        assertEquals("Northbridge Institute of Technology", result.getInstitute());
        assertEquals("SG", result.getCountry());
        assertEquals(AccountVerificationRequestStatus.PENDING, result.getStatus());
        assertEquals("Please verify my instructor account for upcoming software design courses.",
                result.getComments());
        assertNull(result.getCreatedDemoCourseAt());

        verifyPresentInDatabase(AccountVerificationRequest.class, result.getAccountVerificationRequestId());

        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(2, queuedEmails.size());
        assertEquals(EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ADMIN_ALERT, queuedEmails.get(0).getType());
        assertEquals(EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT, queuedEmails.get(1).getType());
    }

    @Test(groups = GroupNames.ACTION)
    public void createAccountVerificationRequestAction_missingEmail_throwsInvalidHttpRequestBodyException() {
        var account = given.account("requester-account");
        persistGivenData(given);

        AccountCreateRequest requestBody = new AccountCreateRequest();
        requestBody.setInstructorName("Dr Amelia Hart");
        requestBody.setInstructorInstitution("Northbridge Institute of Technology");
        requestBody.setInstructorCountry("SG");

        InvalidHttpRequestBodyException exception = assertActionThrows(
                InvalidHttpRequestBodyException.class,
                new RequestContext().withAccountAuth(account.id()).withRequest(requestBody));

        assertEquals("email cannot be null", exception.getMessage());
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
