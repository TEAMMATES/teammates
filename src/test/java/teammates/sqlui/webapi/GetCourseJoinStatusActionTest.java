package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.JoinStatus;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetCourseJoinStatusAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetCourseJoinStatusAction}.
 */
public class GetCourseJoinStatusActionTest extends BaseActionTest<GetCourseJoinStatusAction> {

    private Student typicalStudent;
    private Instructor typicalInstructor;
    private AccountRequest typicalAccountRequest;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUpMethod() {
        typicalStudent = getTypicalStudent();
        typicalInstructor = getTypicalInstructor();
        typicalAccountRequest = getTypicalAccountRequest();
        reset(mockLogic);
    }

    @Test
    void testExecute_studentNotFound_shouldFail() {
        when(mockLogic.getStudentByRegistrationKey("key")).thenReturn(null);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, "key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetCourseJoinStatusAction action = getAction(params);
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::execute);
        assertEquals("No student with given registration key: key", enfe.getMessage());
    }

    @Test
    void testExecute_studentAlreadyRegistered_shouldReturnTrue() {
        when(mockLogic.getStudentByRegistrationKey(typicalStudent.getRegKey())).thenReturn(typicalStudent);

        // mark student as registered
        typicalStudent.setAccount(getTypicalAccount());

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, typicalStudent.getRegKey(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetCourseJoinStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        JoinStatus output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());
    }

    @Test
    void testExecute_studentNotRegistered_shouldReturnFalse() {
        when(mockLogic.getStudentByRegistrationKey(typicalStudent.getRegKey())).thenReturn(typicalStudent);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, typicalStudent.getRegKey(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetCourseJoinStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        JoinStatus output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());
    }

    @Test
    void testExecute_instructorNotFound_shouldFail() {
        when(mockLogic.getInstructorByRegistrationKey("key")).thenReturn(null);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, "key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetCourseJoinStatusAction action = getAction(params);
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::execute);
        assertEquals("No instructor with given registration key: key", enfe.getMessage());
    }

    @Test
    void testExecute_instructorAlreadyRegistered_shouldReturnTrue() {
        when(mockLogic.getInstructorByRegistrationKey(typicalInstructor.getRegKey())).thenReturn(typicalInstructor);

        // mark instructor as registered.
        typicalInstructor.setAccount(getTypicalAccount());

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, typicalInstructor.getRegKey(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetCourseJoinStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        JoinStatus output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());
    }

    @Test
    void testExecute_instructorNotRegistered_shouldReturnFalse() {
        when(mockLogic.getInstructorByRegistrationKey(typicalInstructor.getRegKey())).thenReturn(typicalInstructor);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, typicalInstructor.getRegKey(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        GetCourseJoinStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        JoinStatus output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());
    }

    @Test
    void testExecute_instructorIsCreatingAccountAccountRequestNotFound_shouldFail() {
        when(mockLogic.getAccountRequestByRegistrationKey("key")).thenReturn(null);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, "key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        GetCourseJoinStatusAction action = getAction(params);
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::execute);
        assertEquals("No account request with given registration key: key", enfe.getMessage());
    }

    @Test
    void testExecute_instructorIsCreatingAccountAndNotYetRegistered_shouldReturnFalse() {
        when(mockLogic.getAccountRequestByRegistrationKey(typicalInstructor.getRegKey())).thenReturn(typicalAccountRequest);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, typicalInstructor.getRegKey(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        GetCourseJoinStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        JoinStatus output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());
    }

    @Test
    void testExecute_instructorIsCreatingAccountAndAlreadyRegistered_shouldReturnTrue() {
        typicalAccountRequest.setRegisteredAt(Instant.now());
        when(mockLogic.getAccountRequestByRegistrationKey(typicalInstructor.getRegKey())).thenReturn(typicalAccountRequest);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, typicalInstructor.getRegKey(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        GetCourseJoinStatusAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        JoinStatus output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());
    }

    @Test
    protected void testExecute_invalidParameters_throwsInvalidHttpParameterException() {
        ______TS("invalid parameters");
        loginAsUnregistered("unregistered user");
        verifyHttpParameterFailure();

        verifyHttpParameterFailure(
                Const.ParamsNames.REGKEY, "regkey"
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT
        );

        verifyHttpParameterFailure(
                Const.ParamsNames.ENTITY_TYPE, "some-entity",
                Const.ParamsNames.REGKEY, "regkey"
        );
    }

    @Test
    void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }
}
