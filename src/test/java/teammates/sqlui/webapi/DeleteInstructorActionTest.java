package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteInstructorAction;
import teammates.ui.webapi.InvalidOperationException;

/**
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionTest extends BaseActionTest<DeleteInstructorAction> {

    private Course course;
    private Instructor instructor;
    private Instructor instructor2;
    private Student student;
    private String studentId = "student-googleId";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic);

        course = getTypicalCourse();
        instructor = setupInstructor("instructor-googleId", "instructoremail@teammates.tmt");
        instructor2 = setupInstructor("instructor2-googleId", "instructor2email@teammates.tmt");
        student = getTypicalStudent();

        setupMockLogic();
    }

    private Instructor setupInstructor(String googleId, String email) {
        Account account = getTypicalAccount();
        account.setGoogleId(googleId);
        account.setEmail(email);

        Instructor instructor = getTypicalInstructor();
        instructor.setEmail(email);
        instructor.setDisplayedToStudents(true);
        instructor.setAccount(account);

        return instructor;
    }

    private void setupMockLogic() {
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor2.getGoogleId())).thenReturn(instructor2);
        when(mockLogic.getStudentByGoogleId(course.getId(), studentId)).thenReturn(student);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor2.getEmail())).thenReturn(instructor2);
        when(mockLogic.getInstructorsByCourse(course.getId())).thenReturn(List.of(instructor, instructor2));
        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(List.of(student));
    }

    @Test
    void testExecute_deleteInstructorByGoogleId_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor2.getGoogleId(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), instructor2.getGoogleId());
        verify(mockLogic, times(1)).deleteInstructorCascade(course.getId(), instructor2.getEmail());
        verify(mockLogic, times(1)).deleteInstructorCascade(any(), any());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteInstructorByEmail_success() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor2.getEmail(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorForEmail(course.getId(), instructor2.getEmail());
        verify(mockLogic, times(1)).deleteInstructorCascade(course.getId(), instructor2.getEmail());
        verify(mockLogic, times(1)).deleteInstructorCascade(any(), any());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_onlyOneInstructorInCourse_throwsInvalidOperationException() {
        // Override the mock logic for the course to have only one instructor
        when(mockLogic.getInstructorsByCourse(course.getId())).thenReturn(List.of(instructor));

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        assertEquals(mockLogic.getInstructorsByCourse(course.getId()).size(), 1);

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), instructor.getGoogleId());
        verify(mockLogic, never()).deleteInstructorCascade(any(), any());
    }

    @Test
    void testExecute_onlyOneRegisteredInstructor_throwsInvalidOperationException() {
        instructor2.setAccount(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), instructor.getGoogleId());
        verify(mockLogic, never()).deleteInstructorCascade(any(), any());
    }

    @Test
    void testExecute_onlyOneInstructorDisplayedToStudents_throwsInvalidOperationException() {
        instructor2.setDisplayedToStudents(false);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), instructor.getGoogleId());
        verify(mockLogic, never()).deleteInstructorCascade(any(), any());
    }

    @Test
    void testExecute_instructorDeleteOwnRoleByGoogleId_success() {
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), instructor.getGoogleId());
        verify(mockLogic, times(1)).deleteInstructorCascade(course.getId(), instructor.getEmail());
        verify(mockLogic, times(1)).deleteInstructorCascade(any(), any());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteNonExistentInstructorByGoogleId_failSilently() {
        when(mockLogic.getInstructorByGoogleId(course.getId(), "fake-googleId")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, "fake-googleId",
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorByGoogleId(course.getId(), "fake-googleId");
        verify(mockLogic, never()).deleteInstructorCascade(any(), any());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteNonExistentInstructorByEmail_failSilently() {
        String fakeInstructorEmail = "fake-instructoremail@teammates.tmt";
        when(mockLogic.getInstructorForEmail(course.getId(), fakeInstructorEmail)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, fakeInstructorEmail,
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorForEmail(course.getId(), fakeInstructorEmail);
        verify(mockLogic, never()).deleteInstructorCascade(any(), any());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentCourse_failSilently() {
        String nonExistentCourseId = "non-existent-course-id";
        when(mockLogic.getCourse(nonExistentCourseId)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, nonExistentCourseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorByGoogleId(nonExistentCourseId, instructor.getGoogleId());
        verify(mockLogic, never()).deleteInstructorCascade(any(), any());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingCourseIdWithInstructorId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingCourseIdWithInstructorEmail_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_onlyCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.INSTRUCTOR_ID, instructor.getGoogleId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, params);
        verifyAccessibleWithCorrectSameCoursePrivilege(
                course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, params);
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(
                course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, params);
        verifyAdminsCanAccess(params);
    }
}
