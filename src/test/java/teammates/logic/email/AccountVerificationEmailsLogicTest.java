package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.logic.api.MockTaskQueuer;
import teammates.logic.email.model.AccountVerificationApprovedEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAcknowledgementEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAdminAlertEmailContext;
import teammates.test.BaseTestCase;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link AccountVerificationEmailsLogic}.
 */
public class AccountVerificationEmailsLogicTest extends BaseTestCase {

    private MockTaskQueuer taskQueuer;
    private AccountVerificationEmailsLogic accountVerificationEmailsLogic;

    @BeforeMethod
    public void setUpMethod() {
        taskQueuer = new MockTaskQueuer();
        accountVerificationEmailsLogic = new AccountVerificationEmailsLogic();
        accountVerificationEmailsLogic.init(EmailQueueService.withTaskQueuer(taskQueuer));
    }

    @Test
    public void enqueueCreatedAdminAlertEmail_validContext_enqueuesPriorityEmail() {
        accountVerificationEmailsLogic.enqueueCreatedAdminAlertEmail(
                new AccountVerificationCreatedAdminAlertEmailContext(
                        "admin@teammates.tmt",
                        "Instructor Name",
                        "Institute Name",
                        "instructor@teammates.tmt",
                        "Some comments",
                        "https://example.com/admin/home"));

        assertEnqueuedEmail("admin@teammates.tmt", EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ADMIN_ALERT);
    }

    @Test
    public void enqueueCreatedAcknowledgementEmail_validContext_enqueuesPriorityEmail() {
        accountVerificationEmailsLogic.enqueueCreatedAcknowledgementEmail(
                new AccountVerificationCreatedAcknowledgementEmailContext(
                        "instructor@teammates.tmt",
                        "Instructor Name",
                        "Institute Name",
                        "instructor@teammates.tmt",
                        "Some comments"));

        assertEnqueuedEmail("instructor@teammates.tmt", EmailType.NEW_ACCOUNT_VERIFICATION_REQUEST_ACKNOWLEDGEMENT);
    }

    @Test
    public void enqueueApprovalEmail_validContext_enqueuesPriorityEmailWithSupportBcc() {
        accountVerificationEmailsLogic.enqueueApprovalEmail(
                new AccountVerificationApprovedEmailContext(
                        "instructor@teammates.tmt",
                        "Instructor Name",
                        "https://example.com/instructor/welcome"));

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        EmailWrapper email = request.getEmail();
        assertEquals("instructor@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.ACCOUNT_VERIFICATION_APPROVED, email.getType());
        assertEquals(Config.SUPPORT_EMAIL, email.getBcc());
    }

    private void assertEnqueuedEmail(String expectedRecipient, EmailType expectedType) {
        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        EmailWrapper email = request.getEmail();
        assertEquals(expectedRecipient, email.getRecipient());
        assertEquals(expectedType, email.getType());
    }
}
