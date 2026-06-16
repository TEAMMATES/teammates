package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.TaskWrapper;
import teammates.logic.api.MockTaskQueuer;
import teammates.logic.email.EmailComposer;
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
    private EmailComposer emailComposer;
    private FeedbackSessionsEmailsLogic feedbackSessionsEmailsLogic;

    @BeforeMethod
    public void setUpMethod() {
        taskQueuer = new MockTaskQueuer();
        emailComposer = mock(EmailComposer.class);
        feedbackSessionsEmailsLogic = new FeedbackSessionsEmailsLogic(
                EmailQueueService.withTaskQueuer(taskQueuer),
                emailComposer);
    }

    @Test
    public void enqueueSessionLinksRecoveryEmail_matchingStudentsExist_callsFoundComposerAndEnqueuesPriority() {
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
        EmailWrapper email = createEmail("student@teammates.tmt");
        when(emailComposer.composeSessionLinksRecoveryEmail(context)).thenReturn(email);

        feedbackSessionsEmailsLogic.enqueueSessionLinksRecoveryEmail(context);

        verify(emailComposer).composeSessionLinksRecoveryEmail(context);
        verifyNoMoreInteractions(emailComposer);
        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        assertEquals(email, request.getEmail());
    }

    @Test
    public void enqueueSessionLinksRecoveryEmail_matchingStudentsDoNotExist_callsNotFoundComposerAndEnqueuesPriority() {
        SessionLinksRecoveryContext context = new SessionLinksRecoveryContext(
                "missing@teammates.tmt",
                null,
                true,
                List.of());
        EmailWrapper email = createEmail("missing@teammates.tmt");
        when(emailComposer.composeSessionLinksRecoveryNotFoundEmail("missing@teammates.tmt")).thenReturn(email);

        feedbackSessionsEmailsLogic.enqueueSessionLinksRecoveryEmail(context);

        verify(emailComposer).composeSessionLinksRecoveryNotFoundEmail("missing@teammates.tmt");
        verifyNoMoreInteractions(emailComposer);
        assertEquals(1, taskQueuer.getTasksAdded().size());
        TaskWrapper task = taskQueuer.getTasksAdded().get(0);
        assertEquals(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, task.getQueueName());

        SendEmailRequest request = (SendEmailRequest) task.getRequestBody();
        assertEquals(email, request.getEmail());
    }

    private static EmailWrapper createEmail(String recipientEmailAddress) {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(recipientEmailAddress);
        email.setType(EmailType.SESSION_LINKS_RECOVERY);
        email.setSubject("subject");
        email.setContent("content");
        return email;
    }

}
