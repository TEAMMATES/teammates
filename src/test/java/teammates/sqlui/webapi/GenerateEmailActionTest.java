package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.EmailData;
import teammates.ui.webapi.GenerateEmailAction;

/**
 * SUT: {@link GenerateEmailAction}.
 */
public class GenerateEmailActionTest
        extends BaseActionTest<GenerateEmailAction> {

    private Course course;
    private FeedbackSession session;
    private Student student;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.EMAIL;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        session = new FeedbackSession(
                "session-name", course, "creater_email@tm.tmt", null,
                Instant.parse("2020-01-01T00:00:00.000Z"), Instant.parse("2020-10-01T00:00:00.000Z"),
                Instant.parse("2020-01-01T00:00:00.000Z"), Instant.parse("2020-11-01T00:00:00.000Z"),
                null, false, false, false);
        student = new Student(course, "student name", "student_email@tm.tmt", null);

        loginAsAdmin();
    }

    @Test
    public void testExecute_studentCourseJoinEmailType_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.STUDENT_COURSE_JOIN.name(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
        };

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);

        EmailWrapper email = new EmailWrapper();
        email.setRecipient(student.getEmail());
        email.setType(EmailType.STUDENT_COURSE_JOIN);
        email.setSubjectFromType(course.getName(), course.getId());

        when(mockSqlEmailGenerator.generateStudentCourseJoinEmail(course, student)).thenReturn(email);

        GenerateEmailAction action = getAction(params);
        EmailData actionOutput = (EmailData) getJsonResult(action).getOutput();

        assertEquals(String.format(
                EmailType.STUDENT_COURSE_JOIN.getSubject(),
                course.getName(),
                course.getId()),
                actionOutput.getSubject());
        assertEquals(student.getEmail(), actionOutput.getRecipient());
    }

    @Test
    public void testExecute_feedbackSessionReminderEmailType_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_SESSION_REMINDER.name(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
        };

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getFeedbackSession(session.getName(), course.getId())).thenReturn(session);

        EmailWrapper email = new EmailWrapper();
        email.setRecipient(student.getEmail());
        email.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        email.setSubjectFromType(course.getName(), session.getName());

        List<EmailWrapper> emails = List.of(email);

        when(mockSqlEmailGenerator.generateFeedbackSessionReminderEmails(
                session,
                Collections.singletonList(student),
                new ArrayList<>(), null)).thenReturn(emails);

        GenerateEmailAction action = getAction(params);
        EmailData actionOutput = (EmailData) getJsonResult(action).getOutput();

        assertEquals(String.format(
                EmailType.FEEDBACK_SESSION_REMINDER.getSubject(),
                course.getName(),
                session.getName()),
                actionOutput.getSubject());
        assertEquals(student.getEmail(), actionOutput.getRecipient());
    }

    @Test
    public void testExecute_courseDoesNotExist_throwsEntityNotFoundException() {
        String nonExistCourseId = "non-exist-course-id";
        String[] params = {
                Const.ParamsNames.COURSE_ID, nonExistCourseId,
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.STUDENT_COURSE_JOIN.name(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
        };

        when(mockLogic.getCourse(nonExistCourseId)).thenReturn(null);

        verifyEntityNotFound(params);
    }

    @Test
    public void testExecute_studentDoesNotExist_throwsEntityNotFoundException() {
        String nonExistStudentEmail = "nonExistStudent@tm.tmt";
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, nonExistStudentEmail,
                Const.ParamsNames.EMAIL_TYPE, EmailType.STUDENT_COURSE_JOIN.name(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getName(),
        };

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), nonExistStudentEmail)).thenReturn(null);

        verifyEntityNotFound(params);
    }

    @Test
    public void testExecute_invalidFeedbackSessionname_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_SESSION_REMINDER.name(),
        };

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);

        verifyHttpParameterFailure(params);
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
