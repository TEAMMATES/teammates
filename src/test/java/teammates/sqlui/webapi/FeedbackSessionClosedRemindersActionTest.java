package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.webapi.FeedbackSessionClosedRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest extends BaseActionTest<FeedbackSessionClosedRemindersAction> {

    private Course course;
    private FeedbackSession session;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        session = new FeedbackSession(
                "Session Name",
                course,
                "instructor1email@tm.tmt",
                "Instructions",
                Instant.now().minusSeconds(7200), // Start time 2 hours ago
                Instant.now().minusSeconds(3600), // End time 1 hour ago
                Instant.now().minusSeconds(10800), // Session visible 3 hours ago
                Instant.now().minusSeconds(3600), // Results visible 1 hour ago
                Duration.ofMinutes(30),
                false, true, false
        );

        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of(session));
    }

    @Test
    void testExecute_noSessionsClosed_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosedWithinThePastHour()).thenReturn(List.of());

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_recentlyClosedSession_emailsSent() {
        int numInstructors = 3;
        List<Instructor> instructors = IntStream.range(1, numInstructors + 1)
                .mapToObj(i -> new Instructor(
                        course, "name" + i, "instructor" + i + "email@tm.tmt",
                        false, "", null, new InstructorPrivileges()))
                .collect(Collectors.toList());

        List<EmailWrapper> emails = instructors.stream()
                .map(instructor -> {
                    EmailWrapper email = new EmailWrapper();
                    email.setRecipient(instructor.getEmail());
                    email.setType(EmailType.FEEDBACK_CLOSED);
                    email.setSubjectFromType(course.getName(), session.getName());
                    return email;
                })
                .collect(Collectors.toList());

        when(mockLogic.getInstructorsByCourse(course.getId())).thenReturn(instructors);
        when(mockSqlEmailGenerator.generateFeedbackSessionClosedEmails(session)).thenReturn(emails);

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        // Verify 3 emails were correctly queued
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper emailSent = requestBody.getEmail();
            String expectedSubject = String.format(
                    EmailType.FEEDBACK_CLOSED.getSubject(),
                    course.getName(),
                    session.getName()
            );
            assertEquals(expectedSubject, emailSent.getSubject());
        }

        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_disabledClosedReminderSession_noEmailsSent() {
        session.setClosingEmailEnabled(false);

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_stillInGracePeriodSession_noEmailsSent() {
        session.setEndTime(Instant.now());

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_emailsAlreadySentSession_noEmailsSent() {
        session.setClosedEmailSent(true);

        FeedbackSessionClosedRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
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
    public void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }
}
