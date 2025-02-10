package teammates.sqlui.webapi;

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
    private String studentId = "student-googleId";
    private String instructorId = "instructor-googleId";

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
        Mockito.reset(mockLogic);

        course = new Course("course-id", "Course Name", Const.DEFAULT_TIME_ZONE, "institute");
        student = new Student(course, "Student Name", "studentEmail@gmail.tmt", "Some comments");
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        instructor = new Instructor(course, "Instructor Name", "instructorEmail@tm.tmt",
                false, "", null, instructorPrivileges);

        setupMockLogic();
    }

    private void setupMockLogic() {
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getStudentByGoogleId(course.getId(), studentId)).thenReturn(student);
        when(mockLogic.getStudentForEmail(course.getId(), student.getEmail())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorId)).thenReturn(instructor);
    }

    @Test
    void testExecute_deleteStudentByEmail_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteStudentCascade(course.getId(), student.getEmail());
        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteStudentById_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteStudentCascade(course.getId(), student.getEmail());
        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_courseDoesNotExist_failSilently() {
        when(mockLogic.getCourse("RANDOM_COURSE")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(0)).deleteStudentCascade(course.getId(), student.getEmail());
        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentStudentId_failSilently() {
        when(mockLogic.getStudentByGoogleId(course.getId(), "RANDOM_STUDENT")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, "RANDOM_STUDENT",
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(0)).deleteStudentCascade(course.getId(), student.getEmail());
        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentStudentEmail_failSilently() {
        when(mockLogic.getStudentForEmail(course.getId(), "RANDOM_EMAIL")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, "RANDOM_EMAIL",
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(0)).deleteStudentCascade(course.getId(), student.getEmail());
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
                Const.ParamsNames.STUDENT_ID, studentId,
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
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, studentId,
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
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorInDifferentCourse_cannotAccess() {
        loginAsInstructor("instructor2-googleId");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent(studentId);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        verifyCannotAccess(params);
    }
}
