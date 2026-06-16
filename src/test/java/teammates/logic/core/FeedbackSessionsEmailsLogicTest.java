package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.logic.api.MockTaskQueuer;
import teammates.logic.email.EmailQueueService;
import teammates.logic.email.model.RecoverableCourseLinks;
import teammates.logic.email.model.RecoverableSessionLink;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.test.BaseTestCase;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionsEmailsLogic}.
 */
public class FeedbackSessionsEmailsLogicTest extends BaseTestCase {

    private MockTaskQueuer taskQueuer;
    private FeedbackSessionsEmailsLogic feedbackSessionsEmailsLogic;

    @BeforeMethod
    public void setUpMethod() {
        taskQueuer = new MockTaskQueuer();
        feedbackSessionsEmailsLogic = new FeedbackSessionsEmailsLogic(
                EmailQueueService.withTaskQueuer(taskQueuer));
    }

    @Test
    public void enqueueSessionLinksRecoveryEmail_matchingStudentsExist_enqueuesPriorityRecoveryEmail() {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of(
                        new RecoverableCourseLinks(
                                "CS101",
                                "Software Engineering",
                                List.of(
                                        new RecoverableSessionLink(
                                                "Midterm Feedback",
                                                "https://example.com/submission",
                                                "https://example.com/results")))));

        feedbackSessionsEmailsLogic.enqueueSessionLinksRecoveryEmail(context);

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        EmailWrapper email = request.getEmail();
        assertEquals("student@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY, email.getType());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), email.getSubject());
        assertTrue(email.getContent().contains("Hello Student Name"));
        assertTrue(email.getContent().contains("Midterm Feedback"));
    }

    @Test
    public void enqueueSessionLinksRecoveryEmail_matchingStudentsDoNotExist_enqueuesPriorityNotFoundEmail() {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "missing@teammates.tmt",
                null,
                true,
                List.of());

        feedbackSessionsEmailsLogic.enqueueSessionLinksRecoveryEmail(context);

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        EmailWrapper email = request.getEmail();
        assertEquals("missing@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY, email.getType());
        assertEquals(EmailType.SESSION_LINKS_RECOVERY.getSubject(), email.getSubject());
        assertTrue(email.getContent().contains("Sorry, we could not find any links"));
    }

}
