package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.InstructorCourseJoinEmailWorkerAction;

/**
 * SUT: {@link InstructorCourseJoinEmailWorkerAction}.
 */
public class InstructorCourseJoinEmailWorkerActionTest
        extends BaseActionTest<InstructorCourseJoinEmailWorkerAction> {

    Course course;
    Instructor instructor;
    Account inviter;

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        inviter = new Account("user-id", "inviter name", "account_email@test.tmt");
        loginAsAdmin();
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidInstructor_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
                Const.ParamsNames.INSTRUCTOR_EMAIL, "nonexist-instructor-email@tm.tmt",
        };
        verifyEntityNotFound(params);
    }

    @Test
    public void testExecute_newInstructorJoining_success() {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(instructor.getEmail());
        email.setType(EmailType.INSTRUCTOR_COURSE_JOIN);
        email.setSubjectFromType(course.getName(), course.getId());

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getAccountForGoogleId(inviter.getGoogleId())).thenReturn(inviter);
        when(mockSqlEmailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course)).thenReturn(email);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
                Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "false",
                Const.ParamsNames.INVITER_ID, inviter.getGoogleId(),
        };

        InstructorCourseJoinEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("Successful", actionOutput.getMessage());

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailCreated = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course.getName(),
                course.getId()),
                emailCreated.getSubject());
        assertEquals(instructor.getEmail(), emailCreated.getRecipient());
    }

    @Test
    public void testExecute_oldInstructorRejoining_success() {
        EmailWrapper email = new EmailWrapper();
        email.setRecipient(instructor.getEmail());
        email.setType(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET);
        email.setSubjectFromType(course.getName(), course.getId());

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getAccountForGoogleId(inviter.getGoogleId())).thenReturn(inviter);
        when(mockSqlEmailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor, course))
                .thenReturn(email);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
                Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "true",
        };

        InstructorCourseJoinEmailWorkerAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();
        assertEquals("Successful", actionOutput.getMessage());

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailCreated = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(),
                course.getName(), course.getId()),
                emailCreated.getSubject());
        assertEquals(instructor.getEmail(), emailCreated.getRecipient());
    }

    @Test
    public void testSpecificAccessControl_isAdmin_canAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
                Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "false",
                Const.ParamsNames.INVITER_ID, inviter.getGoogleId(),
        };

        verifyCanAccess(params);
    }

    @Test
    public void testSpecificAccessControl_notAdmin_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
                Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "false",
                Const.ParamsNames.INVITER_ID, inviter.getGoogleId(),
        };

        loginAsInstructor("user-id");
        verifyCannotAccess(params);

        loginAsStudent("user-id");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
