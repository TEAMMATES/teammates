package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.MessageOutput;

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
        when(mockLogic.getInstructor(instructor.getId())).thenReturn(instructor);
        when(mockLogic.getInstructor(instructor2.getId())).thenReturn(instructor2);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor2.getGoogleId())).thenReturn(instructor2);
        when(mockLogic.getStudentByGoogleId(course.getId(), studentId)).thenReturn(student);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(course.getId(), instructor2.getEmail())).thenReturn(instructor2);
        when(mockLogic.getInstructorsByCourse(course.getId())).thenReturn(List.of(instructor, instructor2));
        when(mockLogic.getStudentsForCourse(course.getId())).thenReturn(List.of(student));
    }

    @Test
    void testExecute_deleteInstructorByUserId_success() throws InvalidOperationException {
        String[] params = {
                Const.ParamsNames.USER_ID, instructor2.getId().toString(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteInstructorCascade(instructor2.getId());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_onlyOneInstructorInCourse_throwsInvalidOperationException() throws InvalidOperationException {
        doThrow(new InvalidOperationException(
                "The instructor you are trying to delete is the last instructor in the course. "
                        + "Deleting the last instructor from the course is not allowed."))
                        .when(mockLogic).deleteInstructorCascade(instructor.getId());

        String[] params = {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        verify(mockLogic, times(1)).deleteInstructorCascade(instructor.getId());
    }

    @Test
    void testExecute_instructorDeleteOwnRoleByGoogleId_success() throws InvalidOperationException {
        loginAsInstructor(instructor.getGoogleId());

        String[] params = {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteInstructorCascade(instructor.getId());
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_deleteNonExistentInstructorByUserId_failSilently() throws InvalidOperationException {
        String fakeInstructorId = "00000000-0000-4000-8000-000000000001";

        String[] params = {
                Const.ParamsNames.USER_ID, fakeInstructorId,
        };

        DeleteInstructorAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteInstructorCascade(java.util.UUID.fromString(fakeInstructorId));
        assertEquals("Instructor is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidUserId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.USER_ID, "invalid-user-id",
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, params);
        verifyAccessibleWithCorrectSameCoursePrivilege(
                course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, params);
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(
                course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, params);
        verifyAdminsCanAccess(params);
    }
}
