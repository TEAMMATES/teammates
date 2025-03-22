package teammates.sqlui.webapi;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.ResetAccountAction;

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
        reset(mockLogic);
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
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        Map<String, String> expectedParamMap = new HashMap<>();
        expectedParamMap.put(Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail());
        expectedParamMap.put(Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId());
        expectedParamMap.put(Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "true");

        assertEquals("Account is successfully reset.", output.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertEquals(Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL,
                mockTaskQueuer.getTasksAdded().get(0).getWorkerUrl());
        assertEquals(expectedParamMap, mockTaskQueuer.getTasksAdded().get(0).getParamMap());
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
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        Map<String, String> expectedParamMap = new HashMap<>();
        expectedParamMap.put(Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail());
        expectedParamMap.put(Const.ParamsNames.COURSE_ID, stubStudent.getCourseId());
        expectedParamMap.put(Const.ParamsNames.IS_STUDENT_REJOINING, "true");

        assertEquals("Account is successfully reset.", output.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertEquals(Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_WORKER_URL,
                mockTaskQueuer.getTasksAdded().get(0).getWorkerUrl());
        assertEquals(expectedParamMap, mockTaskQueuer.getTasksAdded().get(0).getParamMap());
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
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        Map<String, String> expectedParamMap = new HashMap<>();
        expectedParamMap.put(Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail());
        expectedParamMap.put(Const.ParamsNames.COURSE_ID, stubStudent.getCourseId());
        expectedParamMap.put(Const.ParamsNames.IS_STUDENT_REJOINING, "true");

        assertEquals("Account is successfully reset.", output.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertEquals(Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_WORKER_URL,
                mockTaskQueuer.getTasksAdded().get(0).getWorkerUrl());
        assertEquals(expectedParamMap, mockTaskQueuer.getTasksAdded().get(0).getParamMap());
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
        ResetAccountAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        MessageOutput output = (MessageOutput) jsonResult.getOutput();

        Map<String, String> expectedParamMap = new HashMap<>();
        expectedParamMap.put(Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail());
        expectedParamMap.put(Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId());
        expectedParamMap.put(Const.ParamsNames.IS_INSTRUCTOR_REJOINING, "true");

        assertEquals("Account is successfully reset.", output.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertEquals(Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL,
                mockTaskQueuer.getTasksAdded().get(0).getWorkerUrl());
        assertEquals(expectedParamMap, mockTaskQueuer.getTasksAdded().get(0).getParamMap());
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
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params1 = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.COURSE_ID, stubStudent.getCourseId(),
        };
        verifyCanAccess(params1);

        String[] params2 = {};
        verifyCanAccess(params2);

        String[] params3 = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
        };
        verifyCanAccess(params3);

        String[] params4 = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyCanAccess(params4);

        String[] params5 = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, stubInstructor.getEmail(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyCanAccess(params5);

        String[] params6 = {
                Const.ParamsNames.COURSE_ID, stubInstructor.getCourseId(),
        };
        verifyCanAccess(params6);

        String[] params7 = {
                "random-params", "random-value",
        };
        verifyCanAccess(params7);
    }

    @Test
    void testSpecificAccessControl_notAdmin_cannotAccess() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
                Const.ParamsNames.COURSE_ID, stubStudent.getCourseId(),
        };
        verifyCannotAccess(params);

        loginAsInstructor(stubInstructor.getGoogleId());
        verifyCannotAccess(params);

        logoutUser();
        loginAsStudent(stubStudent.getGoogleId());
        verifyCannotAccess(params);

        logoutUser();
        loginAsMaintainer();
        verifyCannotAccess(params);

        logoutUser();
        loginAsStudentInstructor(stubStudent.getGoogleId());
        verifyCannotAccess(params);

        logoutUser();
        loginAsUnregistered(stubInstructor.getGoogleId());
        verifyCannotAccess(params);
    }
}
