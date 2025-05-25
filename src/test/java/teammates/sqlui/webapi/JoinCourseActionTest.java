package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JoinCourseAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link JoinCourseAction}.
 */
public class JoinCourseActionTest extends BaseActionTest<JoinCourseAction> {
    private Student stubStudent;
    private Instructor stubInstructor;
    private Course stubCourse;
    private EmailWrapper stubEmailWrapper;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        stubCourse = getTypicalCourse();
        stubInstructor = getTypicalInstructor();
        stubStudent = getTypicalStudent();
        stubEmailWrapper = new EmailWrapper();
        stubEmailWrapper.setRecipient(stubStudent.getEmail());
        stubEmailWrapper.setSubject("You have been registered for " + stubCourse.getName());
        reset(mockLogic);
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        String [] params1 = {};
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.REGKEY, "regkey",
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyHttpParameterFailure(params3);
    }

    @Test
    void testExecute_validStudentRegKey_success() throws EntityAlreadyExistsException, InvalidParametersException,
            EntityDoesNotExistException {
        loginAsUnregistered("unreg-student");

        when(mockLogic.getStudentByRegistrationKey("registered-key-student")).thenReturn(stubStudent);
        when(mockLogic.joinCourseForStudent("registered-key-student", "unreg-student")).thenReturn(stubStudent);
        when(mockLogic.getCourse(stubStudent.getCourseId())).thenReturn(stubCourse);
        when(mockSqlEmailGenerator.generateUserCourseRegisteredEmail(stubStudent.getName(), stubStudent.getEmail(),
                "unreg-student", false, stubCourse)).thenReturn(stubEmailWrapper);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-student",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        JoinCourseAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();
        assertEquals("Student successfully joined course", messageOutput.getMessage());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_studentAlreadyExists_throwsInvalidOperationException() throws EntityAlreadyExistsException,
            InvalidParametersException, EntityDoesNotExistException {
        loginAsUnregistered("unreg-student");

        when(mockLogic.getStudentByRegistrationKey("registered-key-student")).thenReturn(stubStudent);
        when(mockLogic.joinCourseForStudent("registered-key-student", "unreg-student"))
                .thenThrow(EntityAlreadyExistsException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-student",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        JoinCourseAction action = getAction(params);
        assertThrows(InvalidOperationException.class, action::execute);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_studentDoesNotExist_throwsEntityNotFoundException() throws EntityAlreadyExistsException,
            InvalidParametersException, EntityDoesNotExistException {
        loginAsUnregistered("unreg-student");

        when(mockLogic.getStudentByRegistrationKey("invalid-reg-key")).thenReturn(stubStudent);
        when(mockLogic.joinCourseForStudent("invalid-reg-key", "unreg-student"))
                .thenThrow(EntityDoesNotExistException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "invalid-reg-key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        verifyEntityNotFound(params);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_studentInvalidAccount_errorMessage() throws EntityAlreadyExistsException,
            InvalidParametersException, EntityDoesNotExistException {
        loginAsUnregistered("unreg-student");
        when(mockLogic.getStudentByRegistrationKey("invalid-reg-key")).thenReturn(stubStudent);
        when(mockLogic.joinCourseForStudent("invalid-reg-key", "unreg-student"))
                .thenThrow(InvalidParametersException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "invalid-reg-key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };
        JoinCourseAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action, 500);
        assertEquals(500, jsonResult.getStatusCode());
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_validInstructorRegKey_success() throws EntityAlreadyExistsException,
            InvalidParametersException, EntityDoesNotExistException {
        loginAsUnregistered("unreg-instructor");

        when(mockLogic.getInstructorByRegistrationKey("registered-key-instructor")).thenReturn(stubInstructor);
        when(mockLogic.joinCourseForInstructor("registered-key-instructor", "unreg-instructor"))
                .thenReturn(stubInstructor);
        when(mockLogic.getCourse(stubInstructor.getCourseId())).thenReturn(stubCourse);
        when(mockSqlEmailGenerator.generateUserCourseRegisteredEmail(stubInstructor.getName(), stubInstructor.getEmail(),
                "unreg-instructor", true, stubCourse)).thenReturn(stubEmailWrapper);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-instructor",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        JoinCourseAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();
        assertEquals("Instructor successfully joined course", messageOutput.getMessage());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_instructorAlreadyExists_throwsInvalidOperationException() throws EntityAlreadyExistsException,
            InvalidParametersException, EntityDoesNotExistException {
        loginAsUnregistered("unreg-instructor");

        when(mockLogic.getInstructorByRegistrationKey("registered-key-instructor")).thenReturn(stubInstructor);
        when(mockLogic.joinCourseForInstructor("registered-key-instructor", "unreg-instructor"))
                .thenThrow(EntityAlreadyExistsException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-instructor",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        JoinCourseAction action = getAction(params);
        assertThrows(InvalidOperationException.class, action::execute);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_instructorDoesNotExist_throwsEntityNotFoundException() throws EntityDoesNotExistException,
            EntityAlreadyExistsException, InvalidParametersException {
        loginAsUnregistered("unreg-instructor");

        when(mockLogic.getInstructorByRegistrationKey("invalid-reg-key")).thenReturn(stubInstructor);
        when(mockLogic.joinCourseForInstructor("invalid-reg-key", "unreg-instructor"))
                .thenThrow(EntityDoesNotExistException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "invalid-reg-key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        verifyEntityNotFound(params);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_instructorInvalidAccount_errorMessage() throws EntityAlreadyExistsException,
            InvalidParametersException, EntityDoesNotExistException {
        loginAsUnregistered("unreg-instructor");
        when(mockLogic.getInstructorByRegistrationKey("invalid-reg-key")).thenReturn(stubInstructor);
        when(mockLogic.joinCourseForInstructor("invalid-reg-key", "unreg-instructor"))
                .thenThrow(InvalidParametersException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "invalid-reg-key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        JoinCourseAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action, 500);
        assertEquals(500, jsonResult.getStatusCode());
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_invalidEntityType_throwsInvalidHttpParameterException() {
        loginAsUnregistered("unreg-user");

        String[] params1 = {
                Const.ParamsNames.REGKEY, "registered-key-student",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.MAINTAINER,
        };
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.REGKEY, "registered-key-student",
                Const.ParamsNames.ENTITY_TYPE, "invalid-entity-type",
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
                Const.ParamsNames.REGKEY, "registered-key-student",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };
        verifyHttpParameterFailure(params3);

        verifyNoEmailsSent();
    }

    @Test
    void testSpecificAccessControl_loggedIn_canAccess() {
        loginAsUnregistered("unreg-user");
        String[] params = {};
        verifyCanAccess(params);

        logoutUser();
        loginAsAdmin();
        verifyCanAccess(params);

        logoutUser();
        loginAsInstructor("instructor");
        verifyCanAccess(params);

        logoutUser();
        loginAsStudent("student");
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        String[] params = {};
        verifyCannotAccess(params);
    }
}
