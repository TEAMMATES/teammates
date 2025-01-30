package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteStudentAction;

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionTest extends BaseActionTest<DeleteStudentAction> {

    private Course course;
    private Student student;
    private Instructor instructor;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        student = setupStudent("student-googleId", "Student Name", "studentEmail@gmail.tmt", "Some comments");
        instructor = setupInstructor("instructor-googleId", "Instructor Name", "instructorEmail@tm.tmt");

        setupMockLogic();
    }

    private Student setupStudent(String googleId, String name, String email, String comments) {
        Account account = new Account(googleId, name, email);
        Student student = new Student(course, name, email, comments);
        student.setAccount(account);
        return student;
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
        when(mockLogic.getStudentByGoogleId(course.getId(), student.getGoogleId())).thenReturn(student);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
    }

    @Test
    void testExecute_deleteStudentByEmail_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteStudentById_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_courseDoesNotExist_failSilently() {
        when(mockLogic.getCourse("RANDOM_COURSE")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_studentDoesNotExist_failSilently() {
        when(mockLogic.getStudentByGoogleId(course.getId(), "RANDOM_STUDENT")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, "RANDOM_STUDENT",
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidParametersException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingStudentIdOrEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseIdWithStudentId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseIdWithStudentEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_randomEmail_failSilently() {
        when(mockLogic.getStudentForEmail(course.getId(), "RANDOM_EMAIL")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "RANDOM_EMAIL",
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
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
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorInDifferentCourse_cannotAccess() {
        loginAsInstructor("instructor2-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, student.getGoogleId(),
        };

        loginAsStudent(student.getGoogleId());
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
