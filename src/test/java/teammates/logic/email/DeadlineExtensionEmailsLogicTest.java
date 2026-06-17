package teammates.logic.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.logic.api.MockTaskQueuer;
import teammates.logic.email.model.DeadlineExtensionUpdateEmailContext;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionEmailContext;
import teammates.test.BaseTestCase;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link DeadlineExtensionEmailsLogic}.
 */
public class DeadlineExtensionEmailsLogicTest extends BaseTestCase {

    private MockTaskQueuer taskQueuer;
    private DeadlineExtensionEmailsLogic deadlineExtensionEmailsLogic;

    @BeforeMethod
    public void setUpMethod() {
        taskQueuer = new MockTaskQueuer();
        deadlineExtensionEmailsLogic = new DeadlineExtensionEmailsLogic();
        deadlineExtensionEmailsLogic.init(
                EmailQueueService.withTaskQueuer(taskQueuer));
    }

    @Test
    public void enqueueDeadlineExtensionUpdateEmails_validContext_enqueuesStandardEmail() {
        FeedbackSessionEmailContext feedbackSessionContext = new FeedbackSessionEmailContext(
                java.util.UUID.randomUUID(),
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                "Please submit your feedback.",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        DeadlineExtensionUpdateEmailContext emailContext = new DeadlineExtensionUpdateEmailContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                "https://example.com/submission",
                Instant.parse("2027-04-30T15:59:00Z"),
                Instant.parse("2027-04-30T17:00:00Z"),
                EmailType.DEADLINE_EXTENSION_GRANTED);

        deadlineExtensionEmailsLogic.enqueueDeadlineExtensionUpdateEmails(feedbackSessionContext, List.of(emailContext));

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        EmailWrapper email = request.getEmail();
        assertEquals("student@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.DEADLINE_EXTENSION_GRANTED, email.getType());
        assertEquals("TEAMMATES: Deadline extension granted [Course: Software Engineering]"
                + "[Feedback Session: Midterm Feedback]", email.getSubject());
        assertTrue(email.getContent().contains("You have been granted a deadline extension"));
    }
}
