package teammates.logic.email;

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
import teammates.logic.email.model.CourseSessionLinks;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionSummaryEmailContext;
import teammates.logic.email.model.SessionAccessLink;
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
        feedbackSessionsEmailsLogic = new FeedbackSessionsEmailsLogic();
        feedbackSessionsEmailsLogic.init(EmailQueueService.withTaskQueuer(taskQueuer));
    }

    @Test
    public void enqueueSessionLinksRecoveryEmail_matchingStudentsExist_enqueuesPriorityRecoveryEmail() {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "student@teammates.tmt",
                "Student Name",
                false,
                List.of(
                        new CourseSessionLinks(
                                "CS101",
                                "Software Engineering",
                                "Africa/Johannesburg",
                                List.of(
                                        new SessionAccessLink(
                                                "Midterm Feedback",
                                                java.time.Instant.parse("2027-04-30T21:59:00Z"),
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

    @Test
    public void enqueueFeedbackSessionSummaryEmail_validContext_enqueuesPrioritySummaryEmail() {
        FeedbackSessionSummaryEmailContext context = new FeedbackSessionSummaryEmailContext(
                "student@teammates.tmt",
                "Student Name",
                "CS101",
                "Software Engineering",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")),
                false,
                true,
                "https://example.com/join",
                List.of(new CourseSessionLinks(
                        "CS101",
                        "Software Engineering",
                        "Africa/Johannesburg",
                        List.of(new SessionAccessLink(
                                "Midterm Feedback",
                                java.time.Instant.parse("2027-04-30T21:59:00Z"),
                                "https://example.com/submission",
                                "https://example.com/results")))));

        feedbackSessionsEmailsLogic.enqueueFeedbackSessionSummaryEmail(context, EmailType.STUDENT_EMAIL_CHANGED);

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        EmailWrapper email = request.getEmail();
        assertEquals("student@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.STUDENT_EMAIL_CHANGED, email.getType());
        assertEquals("TEAMMATES: Summary of course [Software Engineering][Course ID: CS101]", email.getSubject());
        assertTrue(email.getContent().contains("Midterm Feedback"));
        assertTrue(email.getContent().contains("<table"));
    }

}
