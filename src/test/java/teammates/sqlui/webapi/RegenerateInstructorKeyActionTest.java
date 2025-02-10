package teammates.sqlui.webapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.webapi.RegenerateInstructorKeyAction;

/**
 * SUT: {@link RegenerateInstructorKeyAction}.
 */
public class RegenerateInstructorKeyActionTest extends BaseActionTest<RegenerateInstructorKeyAction> {

    private Course course;
    private Instructor instructor;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    public void setUp() {
        Mockito.reset(mockLogic, mockSqlEmailGenerator, mockEmailSender);
        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = new Instructor(course, "Instructor Name", "instructorEmail@tm.tmt",
                false, "", null, new InstructorPrivileges());
    }

    @Test
    void testExecute_successfulRegenerationWithEmailSent_success()
            throws EntityDoesNotExistException, InstructorUpdateException {
        when(mockLogic.regenerateInstructorRegistrationKey(course.getId(), instructor.getEmail())).thenReturn(instructor);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockSqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                instructor.getEmail(),
                EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED
        )).thenReturn(mockEmail);
        when(mockEmailSender.sendEmail(mockEmail)).thenReturn(
                new EmailSendingStatus(200, "Email sent successfully")
        );

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        RegenerateInstructorKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        assertEquals(actionOutput.getMessage(),
                "Instructor's key for this course has been successfully regenerated, and the email has been sent.");
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_successfulRegenerationWithEmailFailed_success()
            throws EntityDoesNotExistException, InstructorUpdateException {
        when(mockLogic.regenerateInstructorRegistrationKey(course.getId(), instructor.getEmail())).thenReturn(instructor);

        EmailWrapper mockEmail = mock(EmailWrapper.class);
        when(mockSqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                course.getId(),
                instructor.getEmail(),
                EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED
        )).thenReturn(mockEmail);
        when(mockEmailSender.sendEmail(mockEmail)).thenReturn(
                new EmailSendingStatus(500, "Email sending failed")
        );

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        RegenerateInstructorKeyAction action = getAction(params);
        RegenerateKeyData actionOutput = (RegenerateKeyData) getJsonResult(action).getOutput();

        assertEquals(actionOutput.getMessage(),
                "Instructor's key for this course has been successfully regenerated, but the email failed to send.");
        assertNotNull(actionOutput.getNewRegistrationKey());
    }

    @Test
    void testExecute_entityDoesNotExist_throwsEntityNotFoundException()
            throws EntityDoesNotExistException, InstructorUpdateException {
        when(mockLogic.regenerateInstructorRegistrationKey(course.getId(), instructor.getEmail()))
                .thenThrow(new EntityDoesNotExistException("Instructor not found"));

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_instructionUpdateException_throwsInstructorUpdateException()
            throws EntityDoesNotExistException, InstructorUpdateException {
        when(mockLogic.regenerateInstructorRegistrationKey(course.getId(), instructor.getEmail()))
                .thenThrow(new InstructorUpdateException("Instructor update failed"));

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        RegenerateInstructorKeyAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action, HttpStatus.SC_INTERNAL_SERVER_ERROR).getOutput();

        assertEquals(actionOutput.getMessage(), "Regeneration of the instructor's key was unsuccessful.");
    }

    @Test
    void testExecute_noParameters_throwsInvalidParametersException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingInstructorEmail_throwsInvalidParametersException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseId_throwsInvalidParametersException() {
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };
        verifyHttpParameterFailure(params);
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
