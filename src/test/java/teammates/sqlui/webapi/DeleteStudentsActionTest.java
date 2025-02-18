package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteStudentsAction;

/**
 * SUT: {@link DeleteStudentsAction}.
 */
public class DeleteStudentsActionTest extends BaseActionTest<DeleteStudentsAction> {

    private static final int DELETE_LIMIT = 3;
    private Course course;
    private Instructor instructor;
    private String instructorId = "instructor-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic);

        course = getTypicalCourse();
        instructor = getTypicalInstructor();

        setupMockLogic();
    }

    private void setupMockLogic() {
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorId)).thenReturn(instructor);
    }

    @Test
    void testExecute_deleteLimitedStudents_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        DeleteStudentsAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteStudentsInCourseCascade(course.getId());
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentCourse_failSilently() {
        when(mockLogic.getCourse("RANDOM_ID")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "RANDOM_ID",
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        DeleteStudentsAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteStudentsInCourseCascade("RANDOM_ID");
        verify(mockLogic, times(1)).deleteStudentsInCourseCascade(any());
        verify(mockLogic, never()).deleteStudentsInCourseCascade(course.getId());
        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidParametersException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingCourseId_throwsInvalidParametersException() {
        String[] params = {
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingLimit_throwsInvalidParametersException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithInvalidPermission_cannotAccess() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false);
        instructor.setPrivileges(instructorPrivileges);

        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorInDifferentCourse_cannotAccess() {
        loginAsInstructor("instructor2-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        verifyCannotAccess(params);
    }
}
