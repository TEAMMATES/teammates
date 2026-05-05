package teammates.ui.webapi;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link ResetAccountAction}.
 */
public class ResetAccountActionTest extends BaseActionTest<ResetAccountAction> {
    private Student stubStudent;
    private Instructor stubInstructor;
    private Student stubStudentAfterReset;
    private Instructor stubInstructorAfterReset;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_RESET;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        stubStudent = getTypicalStudent();
        stubInstructor = getTypicalInstructor();
        stubInstructor.setAccount(getTypicalAccount());
        stubStudent.setAccount(getTypicalAccount());
        stubStudentAfterReset = getTypicalStudent();
        stubInstructorAfterReset = getTypicalInstructor();
        reset(mockLogic, mockEmailGenerator);
        logoutUser();

        // Mock getCourse to avoid NPE
        when(mockLogic.getCourse(stubStudent.getCourseId())).thenReturn(stubStudent.getCourse());
        when(mockLogic.getCourse(stubInstructor.getCourseId())).thenReturn(stubInstructor.getCourse());
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        String[] params1 = {};
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyHttpParameterFailure(params3);

        String[] params4 = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyHttpParameterFailure(params4);

        String[] params5 = {
                Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId(),
        };
        verifyHttpParameterFailure(params5);
    }

    @Test
    void testExecute_invalidStudentEmail_throwsEntityNotFoundException() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, "invalid-student-email",
                Const.ParamsNames.COURSE_ID, stubStudent.getCourseId(),
        };
        when(mockLogic.getStudentForEmail(stubStudent.getCourseId(), "invalid-student-email"))
                .thenReturn(null);
        verifyEntityNotFound(params);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }

    @Test
    void testExecute_invalidInstructorEmail_throwsEntityNotFoundException() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "invalid-instructor-email",
                Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId(),
        };
        when(mockLogic.getInstructorForEmail(stubInstructor.getCourseId(), "invalid-instructor-email"))
                .thenReturn(null);
        verifyEntityNotFound(params);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }

    @Test
    void testExecute_invalidCourseId_throwsEntityNotFoundException() {
        loginAsAdmin();

        String[] params1 = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };
        when(mockLogic.getStudentForEmail("invalid-course-id", stubStudent.getEmail()))
                .thenReturn(null);
        verifyEntityNotFound(params1);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());

        String[] params2 = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };
        when(mockLogic.getInstructorForEmail("invalid-course-id", stubInstructor.getEmail()))
                .thenReturn(null);
        verifyEntityNotFound(params2);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }

    @Test
    void testExecute_typicalInstructorReset_success() throws EntityDoesNotExistException {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
                Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId(),
        };
        when(mockLogic.getInstructorForEmail(stubInstructor.getCourseId(), stubInstructor.getEmail()))
                .thenReturn(stubInstructor);
        when(mockEmailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(
                stubInstructor, stubInstructor.getCourse())).thenReturn(mock(EmailWrapper.class));
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        assertEquals("Account is successfully reset.", output.getMessage());
        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
        verify(mockLogic, times(1)).resetInstructorGoogleId(stubInstructor.getEmail(),
                stubInstructor.getCourseId(), stubInstructor.getGoogleId());
    }

    @Test
    void testExecute_typicalStudentReset_success() throws EntityDoesNotExistException {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.COURSE_ID, stubStudent.getCourseId(),
        };
        when(mockLogic.getStudentForEmail(stubStudent.getCourseId(), stubStudent.getEmail()))
                .thenReturn(stubStudent);
        when(mockEmailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(
                stubStudent.getCourse(), stubStudent)).thenReturn(mock(EmailWrapper.class));
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        assertEquals("Account is successfully reset.", output.getMessage());
        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
        verify(mockLogic, times(1))
                .resetStudentGoogleId(stubStudent.getEmail(), stubStudent.getCourseId(), stubStudent.getGoogleId());
    }

    @Test
    void testExecute_studentAccountAlreadyReset_failSilently() throws EntityDoesNotExistException {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudentAfterReset.getEmail(),
                Const.ParamsNames.COURSE_ID, stubStudentAfterReset.getCourseId(),
        };
        when(mockLogic.getStudentForEmail(stubStudent.getCourseId(), stubStudent.getEmail()))
                .thenReturn(stubStudentAfterReset);
        when(mockEmailGenerator.generateStudentCourseRejoinEmailAfterGoogleIdReset(
                stubStudent.getCourse(), stubStudentAfterReset)).thenReturn(mock(EmailWrapper.class));
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        assertEquals("Account is successfully reset.", output.getMessage());
        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
        verify(mockLogic, times(0))
                .resetStudentGoogleId(stubStudent.getEmail(), stubStudent.getCourseId(), stubStudent.getGoogleId());
    }

    @Test
    void testExecute_instructorAccountAlreadyReset_failSilently() throws EntityDoesNotExistException {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructorAfterReset.getEmail(),
                Const.ParamsNames.COURSE_ID, stubInstructorAfterReset.getCourseId(),
        };
        when(mockLogic.getInstructorForEmail(stubInstructor.getCourseId(), stubInstructor.getEmail()))
                .thenReturn(stubInstructorAfterReset);
        when(mockEmailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(
                stubInstructorAfterReset, stubInstructor.getCourse())).thenReturn(mock(EmailWrapper.class));
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        assertEquals("Account is successfully reset.", output.getMessage());
        verifySpecifiedTasksAdded(Const.TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, 1);
        verify(mockLogic, times(0)).resetInstructorGoogleId(stubInstructor.getEmail(),
                stubInstructor.getCourseId(), stubInstructor.getGoogleId());
    }

    @Test
    void testExecute_studentResetAccountThrowsEntityDoesNotExistException_throwsEntityNotFoundException()
            throws EntityDoesNotExistException {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.COURSE_ID, stubStudent.getCourseId(),
        };
        when(mockLogic.getStudentForEmail(stubStudent.getCourseId(), stubStudent.getEmail()))
                .thenReturn(stubStudent);
        doThrow(EntityDoesNotExistException.class).when(mockLogic).resetStudentGoogleId(stubStudent.getEmail(),
                stubStudent.getCourseId(), stubStudent.getGoogleId());
        verifyEntityNotFound(params);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());

    }

    @Test
    void testExecute_instructorResetAccountThrowsEntityDoesNotExistException_throwsEntityNotFoundException()
            throws EntityDoesNotExistException {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
                Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId(),
        };
        when(mockLogic.getInstructorForEmail(stubInstructor.getCourseId(), stubInstructor.getEmail()))
                .thenReturn(stubInstructor);
        doThrow(EntityDoesNotExistException.class).when(mockLogic).resetInstructorGoogleId(stubInstructor.getEmail(),
                stubInstructor.getCourseId(), stubInstructor.getGoogleId());
        verifyEntityNotFound(params);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }

    @Test
    void testAccessControl() {
        String[] params1 = {};
        String[] params2 = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.COURSE_ID, stubStudent.getCourseId(),
        };
        String[] params3 = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
        };
        String[] params4 = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        String[] params5 = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        String[] params6 = {
                Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId(),
        };
        String[] params7 = {
                "random-params", "random-value",
        };

        verifyOnlyAdminsCanAccess(params1);
        verifyOnlyAdminsCanAccess(params2);
        verifyOnlyAdminsCanAccess(params3);
        verifyOnlyAdminsCanAccess(params4);
        verifyOnlyAdminsCanAccess(params5);
        verifyOnlyAdminsCanAccess(params6);
        verifyOnlyAdminsCanAccess(params7);
    }
}
