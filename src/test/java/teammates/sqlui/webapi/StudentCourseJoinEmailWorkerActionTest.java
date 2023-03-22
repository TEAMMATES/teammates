package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.StudentCourseJoinEmailWorkerAction;

/**
 * SUT: {@link StudentCourseJoinEmailWorkerAction}.
 */
public class StudentCourseJoinEmailWorkerActionTest
        extends BaseActionTest<StudentCourseJoinEmailWorkerAction> {

    Course course;
    Student student;

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        student = new Student(course, "student name", "student_email@tm.tmt", null);
        loginAsAdmin();
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidStudent_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.STUDENT_EMAIL, "nonexist-student-email@tm.tmt",
        };
        verifyEntityNotFound(params);
    }

    @Test
    public void testExecute_newStudentJoining_success() {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(student.getEmail());
        email.setType(EmailType.STUDENT_COURSE_JOIN);
        email.setSubjectFromType(course.getName(), course.getId());

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);
        when(mockSqlEmailGenerator.generateStudentCourseJoinEmail(course, student)).thenReturn(email);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.IS_STUDENT_REJOINING, "false",
        };

        StudentCourseJoinEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("Successful", actionOutput.getMessage());

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailCreated = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.STUDENT_COURSE_JOIN.getSubject(), course.getName(),
                course.getId()),
                emailCreated.getSubject());
        assertEquals(student.getEmail(), emailCreated.getRecipient());
    }

    @Test
    public void testExecute_oldStudentRejoining_success() {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(student.getEmail());
        email.setType(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET);
        email.setSubjectFromType(course.getName(), course.getId());

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);
        when(mockSqlEmailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, student)).thenReturn(email);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.IS_STUDENT_REJOINING, "true",
        };

        StudentCourseJoinEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("Successful", actionOutput.getMessage());

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailCreated = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.STUDENT_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                course.getName(), course.getId()),
                emailCreated.getSubject());
        assertEquals(student.getEmail(), emailCreated.getRecipient());
    }

    @Test
    public void testSpecificAccessControl_isAdmin_canAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.IS_STUDENT_REJOINING, "false",
        };

        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_notAdmin_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.IS_STUDENT_REJOINING, "false",
        };

        loginAsInstructor("user-id");
        verifyCannotAccess(params);

        loginAsStudent("user-id");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}

