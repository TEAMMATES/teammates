package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteStudentAction}.
 */
public class DeleteStudentActionTest extends BaseActionTest<DeleteStudentAction> {

    private Course course;
    private Student student;
    private Instructor instructor;
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
        when(mockLogic.getStudent(student.getId())).thenReturn(student);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructorId)).thenReturn(instructor);
    }

    @Test
    void testExecute_deleteStudentByUserId_success() {
        String[] params = {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteStudentCascade(student.getId());
        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_nonExistentStudentId_failSilently() {
        String randomUserId = "00000000-0000-4000-8000-000000000001";

        String[] params = {
                Const.ParamsNames.USER_ID, randomUserId,
        };

        DeleteStudentAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).deleteStudentCascade(UUID.fromString(randomUserId));
        assertEquals("Student is successfully deleted.", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidParametersException() {
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
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };

        verifyAdminsCanAccess(params);
        verifyAccessibleWithCorrectSameCoursePrivilege(course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
        verifyInaccessibleWithoutCorrectSameCoursePrivilege(course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, params);
    }
}
