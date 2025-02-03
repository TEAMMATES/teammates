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
import teammates.ui.webapi.FeedbackSessionOpeningRemindersAction;

/**
 * SUT: {@link FeedbackSessionOpeningRemindersAction}.
 */
public class FeedbackSessionOpeningRemindersActionTest extends BaseActionTest<FeedbackSessionOpeningRemindersAction> {

    private Course course;
    private FeedbackSession session;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS;
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
                Instant.now().plusSeconds(3600), // Start time 1 hour later
                Instant.now().plusSeconds(7200), // End time 2 hours later
                Instant.now().minusSeconds(3600), // Session visible 1 hour ago
                Instant.now().plusSeconds(7200), // Results visible in 2 hours
                Duration.ofMinutes(30),
                true, false, false
        );

        when(mockLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent()).thenReturn(List.of(session));
    }

    @Test
    void testExecute_noSessionsOpening_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent()).thenReturn(List.of());

        FeedbackSessionOpeningRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_openingSession_emailsSent() {
        int numRecipients = 3;
        List<EmailWrapper> emails = IntStream.range(1, numRecipients + 1)
                .mapToObj(i -> {
                    EmailWrapper email = new EmailWrapper();
                    email.setRecipient("recipient" + i + "@tm.tmt");
                    email.setType(EmailType.FEEDBACK_OPENING);
                    email.setSubjectFromType(course.getName(), session.getName());
                    return email;
                })
                .collect(Collectors.toList());

        when(mockSqlEmailGenerator.generateFeedbackSessionOpeningEmails(session)).thenReturn(emails);

        FeedbackSessionOpeningRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        // Verify tasks added
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, numRecipients);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        String expectedSubject = String.format(
                EmailType.FEEDBACK_OPENING.getSubject(),
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
    void testExecute_emailsAlreadySentSession_noEmailsSent() {
        session.setOpenEmailSent(true);

        FeedbackSessionOpeningRemindersAction action = getAction();
        MessageOutput output = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", output.getMessage());
    }

    @Test
    void testExecute_sessionOpeningReminderDisabled_noEmailsSent() {
        session.setOpeningEmailEnabled(false);

        FeedbackSessionOpeningRemindersAction action = getAction();
        MessageOutput output = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", output.getMessage());
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
