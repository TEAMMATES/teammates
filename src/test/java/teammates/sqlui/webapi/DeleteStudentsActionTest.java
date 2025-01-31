package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
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
        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        instructor = setupInstructor("instructor-googleId", "name", "instructoremail@tm.tmt");

        setupMockLogic();
    }

    private Instructor setupInstructor(String googleId, String name, String email) {
        Account account = new Account(googleId, name, email);
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);

        Instructor instructor = new Instructor(course, name, email,
                false, "", null, instructorPrivileges);
        instructor.setAccount(account);
        return instructor;
    }

    private void setupMockLogic() {
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
    }

    @Test
    void testExecute_deleteLimitedStudents_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        DeleteStudentsAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_randomCourse_failSilently() {
        when(mockLogic.getCourse("RANDOM_ID")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "RANDOM_ID",
                Const.ParamsNames.LIMIT, String.valueOf(DELETE_LIMIT),
        };

        DeleteStudentsAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Successful", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidParametersException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithInvalidPermission_cannotAccess() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false);
        instructor.setPrivileges(instructorPrivileges);

        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorInDifferentCourse_cannotAccess() {
        loginAsInstructor("instructor2-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        loginAsStudent("student-googleId");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    public void testSpecificAccessControl_loggedOut_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        logoutUser();
        verifyCannotAccess(params);
    }
}
