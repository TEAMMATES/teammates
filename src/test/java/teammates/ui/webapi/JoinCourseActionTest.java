package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.MessageOutput;

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
    }

    @Test
    void testExecute_validStudentRegKey_success() throws EntityAlreadyExistsException,
            EntityDoesNotExistException {
        loginAsUnregistered("unreg-student");

        when(mockLogic.getStudentByRegistrationKey("registered-key-student")).thenReturn(stubStudent);
        when(mockLogic.joinCourse(eq("registered-key-student"), any())).thenReturn(stubStudent);
        when(mockLogic.getCourse(stubStudent.getCourseId())).thenReturn(stubCourse);
        when(mockEmailGenerator.generateUserCourseRegisteredEmail(stubStudent.getName(), stubStudent.getEmail(),
                "unreg-student", false, stubCourse)).thenReturn(stubEmailWrapper);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-student",
        };
        JoinCourseAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();
        assertEquals("User successfully joined course", messageOutput.getMessage());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_studentAlreadyExists_throwsInvalidOperationException() throws EntityAlreadyExistsException,
            EntityDoesNotExistException {
        loginAsUnregistered("unreg-student");

        when(mockLogic.getStudentByRegistrationKey("registered-key-student")).thenReturn(stubStudent);
        when(mockLogic.joinCourse(eq("registered-key-student"), any()))
                .thenThrow(EntityAlreadyExistsException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-student",
        };
        JoinCourseAction action = getAction(params);
        assertThrows(InvalidOperationException.class, action::execute);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_studentDoesNotExist_throwsEntityNotFoundException() throws EntityAlreadyExistsException,
            EntityDoesNotExistException {
        loginAsUnregistered("unreg-student");

        when(mockLogic.getStudentByRegistrationKey("invalid-reg-key")).thenReturn(stubStudent);
        when(mockLogic.joinCourse(eq("invalid-reg-key"), any()))
                .thenThrow(EntityDoesNotExistException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "invalid-reg-key",
        };
        verifyEntityNotFound(params);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_validInstructorRegKey_success() throws EntityAlreadyExistsException,
            EntityDoesNotExistException {
        loginAsUnregistered("unreg-instructor");

        when(mockLogic.getInstructorByRegistrationKey("registered-key-instructor")).thenReturn(stubInstructor);
        when(mockLogic.joinCourse(eq("registered-key-instructor"), any()))
                .thenReturn(stubInstructor);
        when(mockLogic.getCourse(stubInstructor.getCourseId())).thenReturn(stubCourse);
        when(mockEmailGenerator.generateUserCourseRegisteredEmail(stubInstructor.getName(), stubInstructor.getEmail(),
                "unreg-instructor", true, stubCourse)).thenReturn(stubEmailWrapper);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-instructor",
        };
        JoinCourseAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) jsonResult.getOutput();
        assertEquals("User successfully joined course", messageOutput.getMessage());
        verifyNumberOfEmailsSent(1);
    }

    @Test
    void testExecute_instructorAlreadyExists_throwsInvalidOperationException() throws EntityAlreadyExistsException,
            EntityDoesNotExistException {
        loginAsUnregistered("unreg-instructor");

        when(mockLogic.getInstructorByRegistrationKey("registered-key-instructor")).thenReturn(stubInstructor);
        when(mockLogic.joinCourse(eq("registered-key-instructor"), any()))
                .thenThrow(EntityAlreadyExistsException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "registered-key-instructor",
        };
        JoinCourseAction action = getAction(params);
        assertThrows(InvalidOperationException.class, action::execute);
        verifyNoEmailsSent();
    }

    @Test
    void testExecute_instructorDoesNotExist_throwsEntityNotFoundException() throws EntityDoesNotExistException,
            EntityAlreadyExistsException {
        loginAsUnregistered("unreg-instructor");

        when(mockLogic.getInstructorByRegistrationKey("invalid-reg-key")).thenReturn(stubInstructor);
        when(mockLogic.joinCourse(eq("invalid-reg-key"), any()))
                .thenThrow(EntityDoesNotExistException.class);
        String[] params = {
                Const.ParamsNames.REGKEY, "invalid-reg-key",
        };
        verifyEntityNotFound(params);
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
