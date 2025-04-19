package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.FeedbackSessionRemindRequest;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.webapi.FeedbackSessionRemindParticularUsersEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindParticularUsersEmailWorkerAction}.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionRemindParticularUsersEmailWorkerAction> {
    private FeedbackSession session;

    private Instructor instructorToNotify;
    private Instructor instructor;
    private Student student;

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        session = new FeedbackSession(
                "session-name",
                course,
                "creater_email@tm.tmt",
                null,
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.parse("2020-10-01T00:00:00.000Z"),
                Instant.parse("2020-01-01T00:00:00.000Z"),
                Instant.parse("2020-11-01T00:00:00.000Z"),
                null,
                false,
                false,
                false);

        instructorToNotify = new Instructor(course, "to_notify_name", "to_notify_email@tm.tmt", false, "", null, null);
        instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        student = new Student(course, "student name", "student_email@tm.tmt", null);

        loginAsAdmin();
    }

    @Test
    public void testExecute_nonExistentUser_noEmailSent() {
        String instructorToNotifyGoogleId = "instructor-id";

        String courseId = session.getCourse().getId();
        String sessionName = session.getName();

        Course course = session.getCourse();

        String[] usersToRemind = new String[] {
                student.getEmail(),
        };

        EmailWrapper instructorToNotifyEmail = new EmailWrapper();
        instructorToNotifyEmail.setRecipient(instructorToNotify.getEmail());
        instructorToNotifyEmail.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        instructorToNotifyEmail.setSubjectFromType(course.getName(), session.getName());

        List<EmailWrapper> emails = List.of(instructorToNotifyEmail);

        List<Student> students = List.of();
        List<Instructor> instructors = List.of();

        when(mockLogic.getFeedbackSession(sessionName, courseId)).thenReturn(session);

        when(mockLogic.getInstructorByGoogleId(courseId, instructorToNotifyGoogleId)).thenReturn(instructorToNotify);
        when(mockLogic.getStudentForEmail(courseId, student.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorForEmail(courseId, student.getEmail())).thenReturn(null);

        when(mockSqlEmailGenerator.generateFeedbackSessionReminderEmails(
                session, students, instructors, instructorToNotify)).thenReturn(emails);

        FeedbackSessionRemindRequest remindRequest = new FeedbackSessionRemindRequest(courseId,
                sessionName, instructorToNotifyGoogleId, usersToRemind, true);

        FeedbackSessionRemindParticularUsersEmailWorkerAction action = getAction(remindRequest);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Successful", actionOutput.getMessage());

        // Checking Task Queue: only sent to instructorToNotify
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();

            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                    course.getName(), session.getName());
            assertEquals(expectedSubject, email.getSubject());

            String recipient = email.getRecipient();
            assertTrue(recipient.equals(instructorToNotify.getEmail()));
        }
    }

    @Test
    public void testExecute_validUsers_success() {
        String instructorToNotifyGoogleId = "instructor-id";

        String courseId = session.getCourse().getId();
        String sessionName = session.getName();

        Course course = session.getCourse();

        String[] usersToRemind = new String[] {
                student.getEmail(), instructor.getEmail(),
        };

        EmailWrapper studentEmail = new EmailWrapper();
        studentEmail.setRecipient(student.getEmail());
        studentEmail.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        studentEmail.setSubjectFromType(course.getName(), session.getName());

        EmailWrapper instructorEmail = new EmailWrapper();
        instructorEmail.setRecipient(instructor.getEmail());
        instructorEmail.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        instructorEmail.setSubjectFromType(course.getName(), session.getName());

        EmailWrapper instructorToNotifyEmail = new EmailWrapper();
        instructorToNotifyEmail.setRecipient(instructorToNotify.getEmail());
        instructorToNotifyEmail.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        instructorToNotifyEmail.setSubjectFromType(course.getName(), session.getName());

        List<EmailWrapper> emails = List.of(studentEmail, instructorEmail, instructorToNotifyEmail);

        List<Student> students = List.of(student);
        List<Instructor> instructors = List.of(instructor);

        when(mockLogic.getFeedbackSession(sessionName, courseId)).thenReturn(session);

        when(mockLogic.getInstructorByGoogleId(courseId, instructorToNotifyGoogleId)).thenReturn(instructorToNotify);

        when(mockLogic.getStudentForEmail(courseId, student.getEmail())).thenReturn(student);
        when(mockLogic.getStudentForEmail(courseId, instructor.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorForEmail(courseId, student.getEmail())).thenReturn(null);
        when(mockLogic.getInstructorForEmail(courseId, instructor.getEmail())).thenReturn(instructor);

        when(mockSqlEmailGenerator.generateFeedbackSessionReminderEmails(
                session, students, instructors, instructorToNotify)).thenReturn(emails);

        FeedbackSessionRemindRequest remindRequest = new FeedbackSessionRemindRequest(courseId,
                sessionName, instructorToNotifyGoogleId, usersToRemind, true);

        FeedbackSessionRemindParticularUsersEmailWorkerAction action = getAction(remindRequest);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Successful", actionOutput.getMessage());

        // Checking Task Queue
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();

            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                    course.getName(), session.getName());
            assertEquals(expectedSubject, email.getSubject());

            String recipient = email.getRecipient();
            assertTrue(recipient.equals(student.getEmail())
                    || recipient.equals(instructor.getEmail()) || recipient.equals(instructorToNotify.getEmail()));
        }
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
