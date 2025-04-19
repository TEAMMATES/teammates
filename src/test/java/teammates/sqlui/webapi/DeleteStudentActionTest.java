package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

        course = getTypicalCourse();
        student = getTypicalStudent();
        instructor = getTypicalInstructor();

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

        verify(mockLogic, never()).getStudentByGoogleId(any(), any());
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

        verify(mockLogic, times(1)).getStudentByGoogleId(course.getId(), studentId);
        verify(mockLogic, times(1)).deleteStudentCascade(course.getId(), student.getEmail());
        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentCourse_failSilently() {
        when(mockLogic.getCourse("RANDOM_COURSE")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "RANDOM_COURSE",
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getStudentByGoogleId("RANDOM_COURSE", studentId);
        verify(mockLogic, never()).deleteStudentCascade(any(), any());
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

        verify(mockLogic, times(1)).getStudentByGoogleId(course.getId(), "RANDOM_STUDENT");
        verify(mockLogic, never()).deleteStudentCascade(any(), any());
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

        verify(mockLogic, never()).getStudentByGoogleId(any(), any());
        verify(mockLogic, times(1)).deleteStudentCascade(course.getId(), "RANDOM_EMAIL");
        verify(mockLogic, times(1)).deleteStudentCascade(any(), any());
        verify(mockLogic, never()).deleteStudentCascade(course.getId(), student.getEmail());
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
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_ID, studentId,
        };

        verifyAdminsCanAccess(params);
        verifyAccessibleWithCorrectSameCoursePrivilege(course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
    }
}
