package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.webapi.FeedbackSessionOpeningSoonRemindersAction;

/**
 * SUT: {@link FeedbackSessionOpeningSoonRemindersAction}.
 */
public class FeedbackSessionOpeningSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningSoonRemindersAction> {

    private Course course;
    private FeedbackSession session;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void setUp() {
        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        session = new FeedbackSession(
                "Session Name",
                course,
                "instructor1email@tm.tmt",
                "Instructions",
                Instant.now().plus(Duration.ofHours(24)), // Starts in 24 hours
                Instant.now().plus(Duration.ofHours(48)), // Ends in 48 hours
                Instant.now(), // Visible now
                Instant.now().plus(Duration.ofHours(48)), // Results visible in 48 hours
                Duration.ofMinutes(30),
                true, false, false
        );

        when(mockLogic.getFeedbackSessionsOpeningWithinTimeLimit()).thenReturn(List.of(session));
    }

    @Test
    void testExecute_noSessionsOpeningSoon_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsOpeningWithinTimeLimit()).thenReturn(List.of());

        FeedbackSessionOpeningSoonRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_sessionOpeningSoon_emailsSent() {
        int numRecipients = 3;
        List<EmailWrapper> emails = IntStream.range(1, numRecipients + 1)
                .mapToObj(i -> {
                    EmailWrapper email = new EmailWrapper();
                    email.setRecipient("recipient" + i + "@tm.tmt");
                    email.setType(EmailType.FEEDBACK_OPENING_SOON);
                    email.setSubjectFromType(course.getName(), session.getName());
                    return email;
                })
                .collect(Collectors.toList());

        when(mockSqlEmailGenerator.generateFeedbackSessionOpeningSoonEmails(session)).thenReturn(emails);

        FeedbackSessionOpeningSoonRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        // Verify tasks added
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, numRecipients);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        String expectedSubject = String.format(
                EmailType.FEEDBACK_OPENING_SOON.getSubject(),
                course.getName(),
                session.getName()
        );
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest req = (SendEmailRequest) task.getRequestBody();
            EmailWrapper emailSent = req.getEmail();
            assertEquals(expectedSubject, emailSent.getSubject());
        }

        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_sessionOpeningSoonWithEmailsAlreadySent_noEmailsSent() {
        session.setOpeningSoonEmailSent(true);

        FeedbackSessionOpeningSoonRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_openingSoonReminderDisabled_noEmailsSent() {
        session.setOpeningEmailEnabled(false);

        FeedbackSessionOpeningSoonRemindersAction action = getAction();
        MessageOutput output = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", output.getMessage());
    }

    // Checks that no emails are sent when a session is modified to open in < 24 hours if it was initially > 24 hours.
    @Test
    void testExecute_sessionModifiedToOpenInLessThan24Hours_noEmailsSent() {
        session.setStartTime(Instant.now().plusSeconds(3600)); // Starts in 1 hour

        FeedbackSessionOpeningSoonRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    // Checks that sessions previously completed but modified to open in 24 hours will resend the opening soon email.
    @Test
    void testExecute_completedSessionModifiedToOpenIn24Hours_emailsSent() {
        session.setStartTime(Instant.now().minus(Duration.ofHours(2))); // Previously started
        session.setEndTime(Instant.now().minus(Duration.ofHours(1))); // Already ended
        session.setStartTime(Instant.now().plus(Duration.ofHours(24))); // Modified to open in 24 hours
        session.setEndTime(Instant.now().plus(Duration.ofHours(48))); // Modified to end in 48 hours

        int numRecipients = 3;
        List<EmailWrapper> emails = IntStream.range(1, numRecipients + 1)
                .mapToObj(i -> {
                    EmailWrapper email = new EmailWrapper();
                    email.setRecipient("recipient" + i + "@tm.tmt");
                    email.setType(EmailType.FEEDBACK_OPENING_SOON);
                    email.setSubjectFromType(course.getName(), session.getName());
                    return email;
                })
                .collect(Collectors.toList());

        when(mockSqlEmailGenerator.generateFeedbackSessionOpeningSoonEmails(session)).thenReturn(emails);

        FeedbackSessionOpeningSoonRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        // Verify tasks added
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, numRecipients);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        String expectedSubject = String.format(
                EmailType.FEEDBACK_OPENING_SOON.getSubject(),
                course.getName(),
                session.getName()
        );
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest req = (SendEmailRequest) task.getRequestBody();
            EmailWrapper emailSent = req.getEmail();
            assertEquals(expectedSubject, emailSent.getSubject());
        }

        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_instructor_cannotAccess() {
        loginAsInstructor("instructor-googleId");
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }
}
