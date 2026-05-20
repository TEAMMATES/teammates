package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

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

        String[] params6 = {
                Const.ParamsNames.USER_ID, "invalid-user-id",
        };
        verifyHttpParameterFailure(params6);
    }

    @Test
    void testExecute_invalidUserId_throwsEntityNotFoundException() {
        loginAsAdmin();

        UUID invalidUserId = UUID.randomUUID();
        String[] params = {
                Const.ParamsNames.USER_ID, invalidUserId.toString(),
        };
        when(mockLogic.getUser(invalidUserId)).thenReturn(null);
        verifyEntityNotFound(params);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }

    @Test
    void testExecute_typicalInstructorReset_success() throws EntityDoesNotExistException {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.USER_ID, stubInstructor.getId().toString(),
        };
        when(mockLogic.getUser(stubInstructor.getId())).thenReturn(stubInstructor);
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
                Const.ParamsNames.USER_ID, stubStudent.getId().toString(),
        };
        when(mockLogic.getUser(stubStudent.getId())).thenReturn(stubStudent);
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
                Const.ParamsNames.USER_ID, stubStudentAfterReset.getId().toString(),
        };
        when(mockLogic.getUser(stubStudentAfterReset.getId())).thenReturn(stubStudentAfterReset);
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
                Const.ParamsNames.USER_ID, stubInstructorAfterReset.getId().toString(),
        };
        when(mockLogic.getUser(stubInstructorAfterReset.getId())).thenReturn(stubInstructorAfterReset);
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
                Const.ParamsNames.USER_ID, stubStudent.getId().toString(),
        };
        when(mockLogic.getUser(stubStudent.getId())).thenReturn(stubStudent);
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
                Const.ParamsNames.USER_ID, stubInstructor.getId().toString(),
        };
        when(mockLogic.getUser(stubInstructor.getId())).thenReturn(stubInstructor);
        doThrow(EntityDoesNotExistException.class).when(mockLogic).resetInstructorGoogleId(stubInstructor.getEmail(),
                stubInstructor.getCourseId(), stubInstructor.getGoogleId());
        verifyEntityNotFound(params);
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }

    @Test
    void testAccessControl() {
        String[] params1 = {};
        String[] params2 = {
                Const.ParamsNames.USER_ID, stubStudent.getId().toString(),
        };
        String[] params3 = {
                Const.ParamsNames.USER_ID, stubInstructor.getId().toString(),
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
