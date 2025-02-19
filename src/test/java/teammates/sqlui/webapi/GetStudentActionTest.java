package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.StudentData;
import teammates.ui.webapi.GetStudentAction;

/**
 * SUT: {@link GetStudentAction}.
 */
public class GetStudentActionTest extends BaseActionTest<GetStudentAction> {
    private Student stubStudent;
    private Course stubCourse;
    private Instructor stubInstructor;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    protected void setUp() {
        stubStudent = getTypicalStudent();
        Team stubTeam = getTypicalTeam();
        stubStudent.setTeam(stubTeam);
        stubStudent.setRegKey("RANDOM_KEY");
        stubCourse = getTypicalCourse();
        stubInstructor = getTypicalInstructor();
    }

    @Test
    void testExecute_emptyParams_throwsInvalidHttpParameterException() {
        String[] params = {};
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_validParamsUnregisteredStudentNotLoggedIn_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.REGKEY, stubStudent.getRegKey(),
        };
        when(mockLogic.getStudentByRegistrationKey(stubStudent.getRegKey())).thenReturn(stubStudent);
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        assertEquals(studentData.getEmail(), stubStudent.getEmail());
        assertEquals(studentData.getKey(), null);
    }

    @Test
    void testExecute_invalidRegKeyParamsUnregisteredStudent_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.REGKEY, "INVALID_KEY",
        };
        verifyEntityNotFound(params);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyEntityNotFound(params2);

        String[] params3 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.REGKEY, null,
        };

        verifyEntityNotFound(params3);
    }

    @Test
    void testExecute_validParamsRegisteredStudentLoggedIn_success() {
        loginAsStudent(stubStudent.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        assertEquals(studentData.getEmail(), stubStudent.getEmail());
        assertEquals(studentData.getKey(), null);
    }

    @Test
    void testExecute_validParamsRegisteredStudentNotLoggedIn_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),

        };
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        assertEquals(studentData.getEmail(), stubStudent.getEmail());
        assertEquals(studentData.getKey(), null);
    }

    @Test
    void testExecute_invalidStudentEmailParams_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "invalid_email",

        };
        when(mockLogic.getStudentForEmail(stubCourse.getId(), "invalid_email")).thenReturn(null);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_validParamsAdminLoggedIn_success() {
        loginAsAdmin();
        Account stubAccount = getTypicalAccount();
        stubStudent.setAccount(stubAccount);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),

        };
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        assertEquals(studentData.getEmail(), stubStudent.getEmail());
        assertEquals(studentData.getKey(), stubStudent.getRegKey());
        assertEquals(studentData.getGoogleId(), stubStudent.getAccount().getGoogleId());
    }

    @Test
    void testExecute_validParamsInstructorUsingValidStudentEmail_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),

        };
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        assertEquals(studentData.getEmail(), stubStudent.getEmail());
        assertEquals(studentData.getKey(), null);
    }

    @Test
    void testExecute_validParamsInstructorUsingValidStudentGoogleId_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_ID, stubStudent.getGoogleId(),
        };
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        assertEquals(studentData.getEmail(), stubStudent.getEmail());
        assertEquals(studentData.getKey(), null);
    }

    @Test
    void testExecute_validParamsInstructorUsingInvalidStudentGoogleId_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_ID, stubStudent.getGoogleId(),
        };
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(null);
        verifyEntityNotFound(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructor);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyCanAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithoutPermission_cannotAccess() {
        loginAsInstructor(stubInstructor.getGoogleId());
        Instructor stubInstructorWithoutPermission = new Instructor(stubCourse, "name", "google.com",
                false, "googleId",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM, new InstructorPrivileges());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructor.getGoogleId()))
                .thenReturn(stubInstructorWithoutPermission);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentPersonalAccess_canAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentPublicAccess_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubStudent.getGoogleId())).thenReturn(stubStudent);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "randomEmail",
        };
        verifyCannotAccess(params);

        when(mockLogic.getStudentForEmail(stubCourse.getId(), "randomEmail")).thenReturn(stubStudent);
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentNotLoggedInValidRegKey_canAccess() {
        when(mockLogic.getStudentByRegistrationKey(stubStudent.getRegKey())).thenReturn(stubStudent);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.REGKEY, stubStudent.getRegKey(),
        };
        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentNotLoggedInInvalidRegKey_cannotAccess() {
        when(mockLogic.getStudentByRegistrationKey(stubStudent.getRegKey())).thenReturn(null);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.REGKEY, stubStudent.getRegKey(),
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_invalidStudentLoginId_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params);

        loginAsStudent(null);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), null)).thenReturn(null);
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_invalidInstructorLoginId_cannotAccess() {
        loginAsInstructor(null);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params);
    }
}
