package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
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
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Student;
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
                null, false, false);
        student = new Student(course, "student name", "student_email@tm.tmt", null);

        loginAsAdmin();
    }

    @Test
    public void testExecute_studentCourseJoinEmailType_success() {
        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.STUDENT_COURSE_JOIN.name(),
        };

        when(mockLogic.getStudent(student.getId())).thenReturn(student);

        EmailWrapper email = new EmailWrapper();
        email.setRecipient(student.getEmail());
        email.setType(EmailType.STUDENT_COURSE_JOIN);
        email.setSubjectFromType(course.getName(), course.getId());

        when(mockEmailGenerator.generateStudentCourseJoinEmail(course, student)).thenReturn(email);

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
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_SESSION_REMINDER.name(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };

        when(mockLogic.getStudent(student.getId())).thenReturn(student);
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        EmailWrapper email = new EmailWrapper();
        email.setRecipient(student.getEmail());
        email.setType(EmailType.FEEDBACK_SESSION_REMINDER);
        email.setSubjectFromType(course.getName(), session.getName());

        List<EmailWrapper> emails = List.of(email);

        when(mockEmailGenerator.generateFeedbackSessionReminderEmails(
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
    public void testExecute_studentDoesNotExist_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.STUDENT_COURSE_JOIN.name(),
        };

        when(mockLogic.getStudent(student.getId())).thenReturn(null);

        verifyEntityNotFound(params);
    }

    @Test
    public void testExecute_invalidFeedbackSession_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.STUDENT_SQL_ID, student.getId().toString(),
                Const.ParamsNames.EMAIL_TYPE, EmailType.FEEDBACK_SESSION_REMINDER.name(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, "e06ef8a3-d693-40a9-b80c-4560860c3d09",
        };

        when(mockLogic.getStudent(student.getId())).thenReturn(student);
        when(mockLogic.getFeedbackSession(any())).thenReturn(null);

        verifyEntityNotFound(params);
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
    }
}
