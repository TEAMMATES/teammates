package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
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
    private StudentData stubStudentData;

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
        stubStudentData = new StudentData(stubStudent);
        reset(mockLogic);
    }

    /**
     * Enum to represent the type of entity making the request.
     */
    enum Type {
        STUDENT,
        INSTRUCTOR,
        ADMIN
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
        stubStudentData.setInstitute(stubStudent.getCourse().getInstitute());
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        verifyStudentData(stubStudentData, studentData, Type.STUDENT);
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
        stubStudentData.setInstitute(stubStudent.getCourse().getInstitute());
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        verifyStudentData(stubStudentData, studentData, Type.STUDENT);
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
        verifyStudentData(stubStudentData, studentData, Type.INSTRUCTOR);
    }

    @Test
    void testExecute_invalidCourseIdParamsStudent_throwsEntityNotFoundException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "random-course",
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        when(mockLogic.getStudentForEmail("random-course", stubStudent.getEmail())).thenReturn(null);
        verifyEntityNotFound(params);

        loginAsStudent(stubStudent.getGoogleId());
        verifyEntityNotFound(params);
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
    void testExecute_incompleteParamsStudent_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyHttpParameterFailure(params);

        loginAsStudent(stubStudent.getGoogleId());
        verifyHttpParameterFailure(params);
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
        stubStudentData = new StudentData(stubStudent);
        stubStudentData.setKey(stubStudent.getRegKey());
        stubStudentData.setGoogleId(stubStudent.getAccount().getGoogleId());

        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        verifyStudentData(stubStudentData, studentData, Type.ADMIN);
    }

    @Test
    void testExecute_invalidParamsAdminLoggedIn_throwsEntityNotFoundException() {
        loginAsAdmin();
        Account stubAccount = getTypicalAccount();
        stubStudent.setAccount(stubAccount);
        String[] params1 = {
                Const.ParamsNames.COURSE_ID, "random-course",
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),

        };
        when(mockLogic.getStudentForEmail("random-course", stubStudent.getEmail())).thenReturn(null);
        verifyEntityNotFound(params1);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "invalid_email",

        };
        when(mockLogic.getStudentForEmail(stubCourse.getId(), "invalid_email")).thenReturn(null);
        verifyEntityNotFound(params2);
    }

    @Test
    void testExecute_incompleteParamsAdminLoggedOut_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_validParamsInstructor_success() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };

        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        GetStudentAction action = getAction(params);
        StudentData studentData = (StudentData) getJsonResult(action).getOutput();
        verifyStudentData(stubStudentData, studentData, Type.INSTRUCTOR);
    }

    @Test
    void testExecute_invalidCourseIdParamsInstructor_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, "random-course",
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };

        when(mockLogic.getStudentForEmail("random-course", stubStudent.getEmail())).thenReturn(null);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_invalidStudentEmailParamsInstructor_throwsEntityNotFoundException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "invalid_email",
        };

        when(mockLogic.getStudentForEmail(stubCourse.getId(), "invalid-email")).thenReturn(null);
        verifyEntityNotFound(params);
    }

    @Test
    void testExecute_incompleteParamsInstructor_throwsInvalidHttpParameterException() {
        loginAsInstructor(stubInstructor.getGoogleId());
        String[] params1 = {
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        verifyHttpParameterFailure(params1);
    }

    private void verifyStudentData(StudentData expectedStudentData, StudentData actualStudentData, Type reqEntity) {
        assertEquals(expectedStudentData.getEmail(), actualStudentData.getEmail());
        assertEquals(expectedStudentData.getCourseId(), actualStudentData.getCourseId());
        assertEquals(expectedStudentData.getName(), actualStudentData.getName());
        assertEquals(expectedStudentData.getTeamName(), actualStudentData.getTeamName());
        assertEquals(expectedStudentData.getSectionName(), actualStudentData.getSectionName());

        switch (reqEntity) {
        case STUDENT:
            assertNull(actualStudentData.getGoogleId());
            assertNull(actualStudentData.getKey());
            assertNull(actualStudentData.getComments());
            assertNull(actualStudentData.getJoinState());
            assertNotNull(actualStudentData.getInstitute());
            assertEquals(expectedStudentData.getInstitute(), actualStudentData.getInstitute());
            break;
        case INSTRUCTOR:
            assertNull(actualStudentData.getGoogleId());
            assertNull(actualStudentData.getKey());

            assertNotNull(actualStudentData.getComments());
            assertNotNull(actualStudentData.getJoinState());
            assertEquals(expectedStudentData.getComments(), actualStudentData.getComments());
            assertEquals(expectedStudentData.getJoinState(), actualStudentData.getJoinState());
            break;
        case ADMIN:
            assertNotNull(actualStudentData.getGoogleId());
            assertNotNull(actualStudentData.getKey());
            assertNotNull(actualStudentData.getComments());
            assertNotNull(actualStudentData.getJoinState());

            assertEquals(expectedStudentData.getKey(), actualStudentData.getKey());
            assertEquals(expectedStudentData.getGoogleId(), actualStudentData.getGoogleId());
            assertEquals(expectedStudentData.getComments(), actualStudentData.getComments());
            assertEquals(expectedStudentData.getJoinState(), actualStudentData.getJoinState());
            break;
        }

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
    void testSpecificAccessControl_studentPersonalAccessSameCourse_canAccess() {
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
    void testSpecificAccessControl_studentPersonalAccessNotSameCourse_cannotAccess() {
        Student studentFromAnotherCourse = getTypicalStudent();
        studentFromAnotherCourse.setCourse(new Course("another-course", "another-course-name",
                Const.DEFAULT_TIME_ZONE, "teammates"));
        loginAsStudent(studentFromAnotherCourse.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), studentFromAnotherCourse.getGoogleId()))
                .thenReturn(studentFromAnotherCourse);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_studentPublicAccess_cannotAccess() {
        loginAsStudent(stubStudent.getGoogleId());
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        when(mockLogic.getStudentForEmail(stubCourse.getId(), "randomEmail")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "randomEmail",
        };
        verifyCannotAccess(params);

        when(mockLogic.getStudentForEmail(stubCourse.getId(), "randomEmail")).thenReturn(stubStudent);
        verifyCannotAccess(params);

        logoutUser();
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
        String[] params1 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params1);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.STUDENT_EMAIL, stubStudent.getEmail(),
        };
        when(mockLogic.getStudentForEmail(stubCourse.getId(), stubStudent.getEmail())).thenReturn(stubStudent);
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), null)).thenReturn(null);
        verifyCannotAccess(params2);

        logoutUser();
        verifyCannotAccess(params1);
        verifyCannotAccess(params2);
    }
}
