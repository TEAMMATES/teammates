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
import teammates.storage.sqlentity.Team;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.webapi.FeedbackSessionRemindEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionRemindEmailWorkerAction}.
 */
public class FeedbackSessionRemindEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionRemindEmailWorkerAction> {
    private FeedbackSession session;

    private Instructor instructor;
    private Student student;
    private String instructorGoogleId;

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL;
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

        Team team = new Team(null, "team-name");

        instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        student = new Student(course, "student name", "student_email@tm.tmt", null);
        student.setTeam(team);
        instructorGoogleId = "user-id";

        loginAsAdmin();
    }

    @Test
    public void testExecute_allUsersAttempted_success() {
        String courseId = session.getCourse().getId();
        String sessionName = session.getName();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionName,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorGoogleId,
        };

        List<Student> students = List.of(student);
        List<Instructor> instructors = List.of(instructor);

        when(mockLogic.getFeedbackSession(sessionName, courseId)).thenReturn(session);

        when(mockLogic.getStudentsForCourse(courseId)).thenReturn(students);
        when(mockLogic.getInstructorsByCourse(courseId)).thenReturn(instructors);
        when(mockLogic.getInstructorByGoogleId(courseId, instructorGoogleId)).thenReturn(null);

        // Feedback Session not attempted yet by users.
        when(mockLogic.isFeedbackSessionAttemptedByStudent(session, student.getEmail(), student.getTeamName()))
                .thenReturn(true);
        when(mockLogic.isFeedbackSessionAttemptedByInstructor(session, instructor.getEmail())).thenReturn(true);

        List<EmailWrapper> emails = List.of();

        when(mockSqlEmailGenerator.generateFeedbackSessionReminderEmails(session, students, instructors, null))
                .thenReturn(emails);

        FeedbackSessionRemindEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Successful", actionOutput.getMessage());

        // Checking Task Queue
        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_someUserNotYetAttempt_success() {
        String courseId = session.getCourse().getId();
        String sessionName = session.getName();

        Course course = session.getCourse();

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionName,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorGoogleId,
        };

        List<Student> students = List.of(student);
        List<Instructor> instructors = List.of(instructor);

        when(mockLogic.getFeedbackSession(sessionName, courseId)).thenReturn(session);

        when(mockLogic.getStudentsForCourse(courseId)).thenReturn(students);
        when(mockLogic.getInstructorsByCourse(courseId)).thenReturn(instructors);
        when(mockLogic.getInstructorByGoogleId(courseId, instructorGoogleId)).thenReturn(null);

        // Feedback Session not attempted yet by users.
        when(mockLogic.isFeedbackSessionAttemptedByStudent(session, student.getEmail(), student.getTeamName()))
                .thenReturn(false);
        when(mockLogic.isFeedbackSessionAttemptedByInstructor(session, instructor.getEmail())).thenReturn(false);

        EmailWrapper studentEmail = new EmailWrapper();
        studentEmail.setRecipient(student.getEmail());
        studentEmail.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        studentEmail.setSubjectFromType(course.getName(), session.getName());

        EmailWrapper instructorEmail = new EmailWrapper();
        instructorEmail.setRecipient(instructor.getEmail());
        instructorEmail.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        instructorEmail.setSubjectFromType(course.getName(), session.getName());

        List<EmailWrapper> emails = List.of(studentEmail, instructorEmail);

        when(mockSqlEmailGenerator.generateFeedbackSessionReminderEmails(session, students, instructors, null))
                .thenReturn(emails);

        FeedbackSessionRemindEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Successful", actionOutput.getMessage());

        // Checking Task Queue
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                    course.getName(), session.getName());
            assertEquals(expectedSubject, email.getSubject());

            String recipient = email.getRecipient();
            assertTrue(recipient.equals(student.getEmail()) || recipient.equals(instructor.getEmail()));
        }
    }

    @Test
    void testAccessControl() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
                Const.ParamsNames.COURSE_ID, session.getCourse().getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructorGoogleId,
        };
        verifyOnlyAdminsCanAccess(params);
    }
}
