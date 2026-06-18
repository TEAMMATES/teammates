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
import teammates.logic.email.model.FeedbackSessionOwnerReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionParticipantReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionPreviewReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionResultsParticipantEmailContext;
import teammates.logic.email.model.FeedbackSessionResultsPreviewEmailContext;
import teammates.logic.email.model.FeedbackSessionSummaryEmailContext;
import teammates.logic.email.model.SessionAccessLink;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.test.BaseTestCase;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionEmailsLogic}.
 */
public class FeedbackSessionEmailsLogicTest extends BaseTestCase {

    private MockTaskQueuer taskQueuer;
    private FeedbackSessionEmailsLogic feedbackSessionEmailsLogic;

    @BeforeMethod
    public void setUpMethod() {
        taskQueuer = new MockTaskQueuer();
        feedbackSessionEmailsLogic = new FeedbackSessionEmailsLogic();
        feedbackSessionEmailsLogic.init(EmailQueueService.withTaskQueuer(taskQueuer));
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

        feedbackSessionEmailsLogic.enqueueSessionLinksRecoveryEmail(context);

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

        feedbackSessionEmailsLogic.enqueueSessionLinksRecoveryEmail(context);

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

        feedbackSessionEmailsLogic.enqueueFeedbackSessionSummaryEmail(context, EmailType.STUDENT_EMAIL_CHANGED);

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

    @Test
    public void enqueueOpenedEmails_validContexts_enqueuesStandardEmails() {
        var participantContext = new FeedbackSessionParticipantReminderEmailContext(
                "student@teammates.tmt",
                "Student Name",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                false,
                "Please submit your feedback.",
                "https://example.com/submission",
                false,
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        var previewContext = new FeedbackSessionPreviewReminderEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                "Please submit your feedback.",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));

        feedbackSessionEmailsLogic.enqueueOpenedEmails(List.of(participantContext), List.of(previewContext));

        assertEquals(2, taskQueuer.getTasksAdded().size());
        TaskWrapper participantTask = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, participantTask.getQueueName());
        EmailWrapper participantEmail = ((SendEmailRequest) participantTask.getRequestBody()).getEmail();
        assertEquals("student@teammates.tmt", participantEmail.getRecipient());
        assertEquals(EmailType.FEEDBACK_OPENED, participantEmail.getType());

        TaskWrapper previewTask = taskQueuer.getTasksAdded().get(1);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, previewTask.getQueueName());
        EmailWrapper previewEmail = ((SendEmailRequest) previewTask.getRequestBody()).getEmail();
        assertEquals("instructor@teammates.tmt", previewEmail.getRecipient());
        assertEquals(EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX
                + "TEAMMATES: Feedback session now open [Course: Software Engineering][Feedback Session: Midterm Feedback]",
                previewEmail.getSubject());
    }

    @Test
    public void enqueueClosingSoonEmails_validContexts_enqueuesStandardEmails() {
        var participantContext = new FeedbackSessionParticipantReminderEmailContext(
                "student@teammates.tmt",
                "Student Name",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                true,
                "Please submit your feedback.",
                "https://example.com/submission",
                false,
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        var previewContext = new FeedbackSessionPreviewReminderEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                "Please submit your feedback.",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));

        feedbackSessionEmailsLogic.enqueueClosingSoonEmails(List.of(participantContext), List.of(previewContext));

        assertEquals(2, taskQueuer.getTasksAdded().size());
        TaskWrapper participantTask = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, participantTask.getQueueName());
        EmailWrapper participantEmail = ((SendEmailRequest) participantTask.getRequestBody()).getEmail();
        assertEquals("student@teammates.tmt", participantEmail.getRecipient());
        assertEquals(EmailType.FEEDBACK_CLOSING_SOON, participantEmail.getType());

        TaskWrapper previewTask = taskQueuer.getTasksAdded().get(1);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, previewTask.getQueueName());
        EmailWrapper previewEmail = ((SendEmailRequest) previewTask.getRequestBody()).getEmail();
        assertEquals("instructor@teammates.tmt", previewEmail.getRecipient());
        assertEquals(EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX
                + "TEAMMATES: Feedback session closing soon [Course: Software Engineering]"
                + "[Feedback Session: Midterm Feedback]",
                previewEmail.getSubject());
    }

    @Test
    public void enqueueSubmissionReminderEmails_validContexts_enqueuesPriorityEmails() {
        var participantContext = new FeedbackSessionParticipantReminderEmailContext(
                "student@teammates.tmt",
                "Student Name",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                false,
                "Please submit your feedback.",
                "https://example.com/submission",
                false,
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        var previewContext = new FeedbackSessionPreviewReminderEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                "Please submit your feedback.",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));

        feedbackSessionEmailsLogic.enqueueSubmissionReminderEmails(List.of(participantContext), List.of(previewContext));

        assertEquals(2, taskQueuer.getTasksAdded().size());
        TaskWrapper participantTask = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, participantTask.getQueueName());
        EmailWrapper participantEmail = ((SendEmailRequest) participantTask.getRequestBody()).getEmail();
        assertEquals("student@teammates.tmt", participantEmail.getRecipient());
        assertEquals(EmailType.FEEDBACK_SESSION_REMINDER, participantEmail.getType());

        TaskWrapper previewTask = taskQueuer.getTasksAdded().get(1);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, previewTask.getQueueName());
        EmailWrapper previewEmail = ((SendEmailRequest) previewTask.getRequestBody()).getEmail();
        assertEquals("instructor@teammates.tmt", previewEmail.getRecipient());
        assertEquals(EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX
                + "TEAMMATES: Feedback session reminder [Course: Software Engineering][Feedback Session: Midterm Feedback]",
                previewEmail.getSubject());
    }

    @Test
    public void enqueuePublishedEmails_validContexts_enqueuesStandardEmails() {
        var participantContext = new FeedbackSessionResultsParticipantEmailContext(
                "student@teammates.tmt",
                "Student Name",
                "CS101",
                "Software Engineering",
                "Midterm Feedback",
                "https://example.com/results",
                false,
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        var previewContext = new FeedbackSessionResultsPreviewEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Midterm Feedback",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));

        feedbackSessionEmailsLogic.enqueuePublishedEmails(List.of(participantContext), List.of(previewContext));

        assertEquals(2, taskQueuer.getTasksAdded().size());
        TaskWrapper participantTask = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, participantTask.getQueueName());
        EmailWrapper participantEmail = ((SendEmailRequest) participantTask.getRequestBody()).getEmail();
        assertEquals("student@teammates.tmt", participantEmail.getRecipient());
        assertEquals(EmailType.FEEDBACK_PUBLISHED, participantEmail.getType());

        TaskWrapper previewTask = taskQueuer.getTasksAdded().get(1);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, previewTask.getQueueName());
        EmailWrapper previewEmail = ((SendEmailRequest) previewTask.getRequestBody()).getEmail();
        assertEquals("instructor@teammates.tmt", previewEmail.getRecipient());
        assertEquals(EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX
                + "TEAMMATES: Feedback session results published [Course: Software Engineering]"
                + "[Feedback Session: Midterm Feedback]",
                previewEmail.getSubject());
    }

    @Test
    public void enqueuePublishedReminderEmails_validContexts_enqueuesPriorityEmails() {
        var participantContext = new FeedbackSessionResultsParticipantEmailContext(
                "student@teammates.tmt",
                "Student Name",
                "CS101",
                "Software Engineering",
                "Midterm Feedback",
                "https://example.com/results",
                false,
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        var previewContext = new FeedbackSessionResultsPreviewEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Midterm Feedback",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));

        feedbackSessionEmailsLogic.enqueuePublishedReminderEmails(List.of(participantContext), List.of(previewContext));

        assertEquals(2, taskQueuer.getTasksAdded().size());
        TaskWrapper participantTask = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, participantTask.getQueueName());
        TaskWrapper previewTask = taskQueuer.getTasksAdded().get(1);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, previewTask.getQueueName());
        EmailWrapper previewEmail = ((SendEmailRequest) previewTask.getRequestBody()).getEmail();
        assertTrue(previewEmail.getIsCopy());
    }

    @Test
    public void enqueueUnpublishedEmails_validContexts_enqueuesStandardEmails() {
        var participantContext = new FeedbackSessionResultsParticipantEmailContext(
                "student@teammates.tmt",
                "Student Name",
                "CS101",
                "Software Engineering",
                "Midterm Feedback",
                null,
                false,
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));
        var previewContext = new FeedbackSessionResultsPreviewEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Midterm Feedback",
                List.of(new EmailContact("Instructor One", "instructor1@teammates.tmt")));

        feedbackSessionEmailsLogic.enqueueUnpublishedEmails(List.of(participantContext), List.of(previewContext));

        assertEquals(2, taskQueuer.getTasksAdded().size());
        TaskWrapper participantTask = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, participantTask.getQueueName());
        EmailWrapper participantEmail = ((SendEmailRequest) participantTask.getRequestBody()).getEmail();
        assertEquals("student@teammates.tmt", participantEmail.getRecipient());
        assertEquals(EmailType.FEEDBACK_UNPUBLISHED, participantEmail.getType());

        TaskWrapper previewTask = taskQueuer.getTasksAdded().get(1);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, previewTask.getQueueName());
        EmailWrapper previewEmail = ((SendEmailRequest) previewTask.getRequestBody()).getEmail();
        assertEquals("instructor@teammates.tmt", previewEmail.getRecipient());
        assertEquals(EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX
                + "TEAMMATES: Feedback session results unpublished [Course: Software Engineering]"
                + "[Feedback Session: Midterm Feedback]",
                previewEmail.getSubject());
    }

    @Test
    public void enqueueOpeningSoonEmails_validContexts_enqueuesStandardEmails() {
        var ownerContext = new FeedbackSessionOwnerReminderEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-29T15:00:00Z"),
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                "Please submit your feedback.",
                "https://example.com/edit",
                null,
                null);

        feedbackSessionEmailsLogic.enqueueOpeningSoonEmails(List.of(ownerContext));

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, task.getQueueName());
        EmailWrapper email = ((SendEmailRequest) task.getRequestBody()).getEmail();
        assertEquals("instructor@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_OPENING_SOON, email.getType());
    }

    @Test
    public void enqueueClosedEmails_validContexts_enqueuesStandardEmails() {
        var ownerContext = new FeedbackSessionOwnerReminderEmailContext(
                "instructor@teammates.tmt",
                "Instructor One",
                "CS101",
                "Software Engineering",
                "Asia/Singapore",
                "Midterm Feedback",
                java.time.Instant.parse("2027-04-29T15:00:00Z"),
                java.time.Instant.parse("2027-04-30T15:59:00Z"),
                "Please submit your feedback.",
                null,
                "https://example.com/report",
                null);

        feedbackSessionEmailsLogic.enqueueClosedEmails(List.of(ownerContext));

        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.SEND_EMAIL_QUEUE_NAME, task.getQueueName());
        EmailWrapper email = ((SendEmailRequest) task.getRequestBody()).getEmail();
        assertEquals("instructor@teammates.tmt", email.getRecipient());
        assertEquals(EmailType.FEEDBACK_CLOSED, email.getType());
    }

}
