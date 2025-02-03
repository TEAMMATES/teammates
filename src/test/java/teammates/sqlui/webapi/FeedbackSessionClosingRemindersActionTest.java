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
import teammates.common.util.TimeHelperExtension;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.webapi.FeedbackSessionClosingRemindersAction;

/**
 * SUT: {@link FeedbackSessionClosingRemindersAction}.
 */
public class FeedbackSessionClosingRemindersActionTest extends BaseActionTest<FeedbackSessionClosingRemindersAction> {

    private Course course;
    private FeedbackSession session;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS;
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
                Instant.now().minusSeconds(7200), // Start time 2 hours ago
                Instant.now().plusSeconds(3600), // End time 1 hour later
                Instant.now().minusSeconds(10800), // Session visible 3 hours ago
                Instant.now().plusSeconds(3600), // Results visible 1 hour later
                Duration.ofMinutes(30),
                false, true, false
        );

        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of(session));
        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of());
    }

    @Test
    void testExecute_noSessionsClosingSoon_noEmailsSent() {
        when(mockLogic.getFeedbackSessionsClosingWithinTimeLimit()).thenReturn(List.of());

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_closingSoonSession_emailsSent() {
        int numRecipients = 3;
        List<EmailWrapper> emails = IntStream.range(1, numRecipients + 1)
                .mapToObj(i -> {
                    EmailWrapper email = new EmailWrapper();
                    email.setRecipient("recipient" + i + "@tm.tmt");
                    email.setType(EmailType.FEEDBACK_CLOSING);
                    email.setSubjectFromType(course.getName(), session.getName());
                    return email;
                })
                .collect(Collectors.toList());

        when(mockSqlEmailGenerator.generateFeedbackSessionClosingEmails(session)).thenReturn(emails);

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        // Verify emails are scheduled for sending
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, numRecipients);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        String expectedSubject = String.format(
                EmailType.FEEDBACK_CLOSING.getSubject(),
                course.getName(),
                session.getName()
        );
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper emailSent = requestBody.getEmail();
            assertEquals(expectedSubject, emailSent.getSubject());
        }

        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_closingReminderDisabledSession_noEmailsSent() {
        session.setClosingEmailEnabled(false);

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_notYetOpenedSession_noEmailsSent() {
        session.setStartTime(Instant.now().plusSeconds(3600)); // Start time 1 hour later

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_emailsAlreadySentSession_noEmailsSent() {
        session.setClosingSoonEmailSent(true);

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_validDeadlineExtensions_emailsSent() {
        int numInstructors = 3;
        int numStudents = 3;

        List<Instructor> instructors = IntStream.range(1, numInstructors + 1)
                .mapToObj(i -> new Instructor(
                        course, "Instructor name " + i, "instructor" + i + "email@tm.tmt",
                        false, "", null, new InstructorPrivileges()))
                .collect(Collectors.toList());

        List<Student> students = IntStream.range(1, numStudents + 1)
                .mapToObj(i -> new Student(
                        course, "Student name " + i, "student" + i + "email@tm.tmt", "Some Comments"))
                .collect(Collectors.toList());

        Instant extendedDeadlineTime = TimeHelperExtension.getInstantHoursOffsetFromNow(16);
        DeadlineExtension deadlineExtensionStudent1 = new DeadlineExtension(students.get(0), session, extendedDeadlineTime);
        DeadlineExtension deadlineExtensionStudent2 = new DeadlineExtension(students.get(1), session, extendedDeadlineTime);
        DeadlineExtension deadlineExtensionInstructor1 =
                new DeadlineExtension(instructors.get(0), session, extendedDeadlineTime);
        List<DeadlineExtension> deadlineExtensions =
                List.of(deadlineExtensionStudent1, deadlineExtensionStudent2, deadlineExtensionInstructor1);

        List<EmailWrapper> emails = deadlineExtensions.stream()
                .map(de -> {
                    EmailWrapper email = new EmailWrapper();
                    email.setRecipient(de.getUser().getEmail());
                    email.setType(EmailType.FEEDBACK_CLOSING);
                    email.setSubjectFromType(course.getName(), session.getName());
                    return email;
                })
                .collect(Collectors.toList());

        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(deadlineExtensions);
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingWithExtensionEmails(session, deadlineExtensions))
                .thenReturn(emails);

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        // Verify emails are scheduled for sending
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, deadlineExtensions.size());

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        String expectedSubject = String.format(
                EmailType.FEEDBACK_CLOSING.getSubject(),
                course.getName(),
                session.getName()
        );
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper emailSent = requestBody.getEmail();
            assertEquals(expectedSubject, emailSent.getSubject());
        }

        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_deadlineExtensionsReminderDisabled_noEmailsSent() {
        session.setClosingEmailEnabled(false);

        Student student = new Student(course, "Student name", "studentEmail@tm.tmt", "Some Comments");
        DeadlineExtension deadlineExtension = new DeadlineExtension(student, session, Instant.now().plusSeconds(3600));

        EmailWrapper email = new EmailWrapper();
        email.setRecipient(student.getEmail());
        email.setType(EmailType.FEEDBACK_CLOSING);
        email.setSubjectFromType(course.getName(), session.getName());

        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of(deadlineExtension));
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingWithExtensionEmails(session, List.of(deadlineExtension)))
                .thenReturn(List.of(email));

        FeedbackSessionClosingRemindersAction action = getAction();
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verifyNoTasksAdded();
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_invalidDeadlineExtensions_noEmailsSent() {
        Student student = new Student(course, "Student name", "studentEmail@tm.tmt", "Some Comments");
        DeadlineExtension deadlineExtension = new DeadlineExtension(student, session, Instant.now().minusSeconds(3600));

        when(mockLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail()).thenReturn(List.of(deadlineExtension));
        when(mockSqlEmailGenerator.generateFeedbackSessionClosingWithExtensionEmails(session, List.of(deadlineExtension)))
                .thenReturn(List.of());

        FeedbackSessionClosingRemindersAction action = getAction();
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
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }
}
