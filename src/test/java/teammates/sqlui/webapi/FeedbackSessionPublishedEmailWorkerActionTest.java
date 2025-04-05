package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.webapi.FeedbackSessionPublishedEmailWorkerAction;

/**
 * SUT: {@link FeedbackSessionPublishedEmailWorkerAction}.
 */
public class FeedbackSessionPublishedEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionPublishedEmailWorkerAction> {
    private FeedbackSession session;

    private Instructor instructor;
    private Student student;

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_URL;
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

        instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        student = new Student(course, "student name", "student_email@tm.tmt", null);

        loginAsAdmin();
    }

    @Test
    public void testExecute_sessionDoesNotExist_failure() {
        String courseId = session.getCourse().getId();
        String sessionName = session.getName();

        when(mockLogic.getFeedbackSession(sessionName, courseId)).thenReturn(null);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionName,
        };

        FeedbackSessionPublishedEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Failure", actionOutput.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_sessionExists_success() throws EntityDoesNotExistException, InvalidParametersException {
        String courseId = session.getCourse().getId();
        String sessionName = session.getName();

        Course course = session.getCourse();

        EmailWrapper studentEmail = new EmailWrapper();
        studentEmail.setRecipient(student.getEmail());
        studentEmail.setType(EmailType.FEEDBACK_PUBLISHED);
        studentEmail.setSubjectFromType(course.getName(), session.getName());

        EmailWrapper instructorEmail = new EmailWrapper();
        instructorEmail.setRecipient(instructor.getEmail());
        instructorEmail.setType(EmailType.FEEDBACK_PUBLISHED);
        instructorEmail.setSubjectFromType(course.getName(), session.getName());

        List<EmailWrapper> emails = List.of(studentEmail, instructorEmail);

        session.setPublishedEmailSent(false);

        FeedbackSession expectedSession = new FeedbackSession(
                session.getName(), session.getCourse(), session.getCreatorEmail(), session.getInstructions(),
                session.getStartTime(), session.getEndTime(), session.getSessionVisibleFromTime(),
                session.getResultsVisibleFromTime(), session.getGracePeriod(), session.isOpenedEmailEnabled(),
                session.isClosingSoonEmailEnabled(), session.isPublishedEmailEnabled());

        expectedSession.setPublishedEmailSent(true);

        when(mockLogic.getFeedbackSession(sessionName, courseId)).thenReturn(session);
        when(mockSqlEmailGenerator.generateFeedbackSessionPublishedEmails(session)).thenReturn(emails);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionName,
        };

        FeedbackSessionPublishedEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Successful", actionOutput.getMessage());

        // Checking Task Queue
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 2);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_PUBLISHED.getSubject(),
                    course.getName(), session.getName());
            assertEquals(expectedSubject, email.getSubject());
        }
    }

    @Test
    void testAccessControl() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourse().getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
        };
        verifyOnlyAdminsCanAccess(params);
    }
}
