package teammates.ui.webapi;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TaskWrapper;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link CreateInstructorAction}.
 */
public class CreateInstructorActionTest extends BaseActionTest<CreateInstructorAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        String courseId = instructor1OfCourse1.getCourseId();

        ______TS("Typical case: add an instructor successfully");

        loginAsInstructor(instructorId);

        String newInstructorName = "New Instructor Name";
        String newInstructorEmail = "ICIAAT.newInstructor@email.tmt";

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        InstructorCreateRequest reqBody = new InstructorCreateRequest(instructorId, newInstructorName, newInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        CreateInstructorAction createInstructorAction = getAction(reqBody, submissionParams);
        JsonResult actionOutput = getJsonResult(createInstructorAction);

        InstructorData response = (InstructorData) actionOutput.getOutput();

        assertNotNull(logic.getInstructorForEmail(courseId, newInstructorEmail));

        InstructorAttributes instructorAdded = logic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.getName());
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, instructorAdded.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());

        verifySpecifiedTasksAdded(Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        TaskWrapper taskAdded = mockTaskQueuer.getTasksAdded().get(0);

        assertEquals(courseId, taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID));
        assertEquals(instructorAdded.getEmail(), reqBody.getEmail());
        assertEquals(instructorId, reqBody.getId());

        ______TS("Error: try to add an existing instructor");

        InvalidOperationException ioe = verifyInvalidOperation(reqBody, submissionParams);
        assertEquals("An instructor with the same email address already exists in the course.",
                ioe.getMessage());

        verifyNoTasksAdded();

        ______TS("Error: try to add an instructor with invalid email");

        String newInvalidInstructorEmail = "ICIAAT.newInvalidInstructor.email.tmt";
        reqBody = new InstructorCreateRequest(null, newInstructorName, newInvalidInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(reqBody, submissionParams);
        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, newInvalidInstructorEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                FieldValidator.EMAIL_MAX_LENGTH),
                ihrbe.getMessage());

        verifyNoTasksAdded();

        ______TS("Masquerade mode: add an instructor");

        logic.deleteInstructorCascade(courseId, newInstructorEmail);

        loginAsAdmin();
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        reqBody = new InstructorCreateRequest(instructorId, newInstructorName, newInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        createInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(createInstructorAction);

        response = (InstructorData) actionOutput.getOutput();

        assertNotNull(logic.getInstructorForEmail(courseId, newInstructorEmail));

        instructorAdded = logic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.getName());
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, instructorAdded.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());

        verifySpecifiedTasksAdded(Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        taskAdded = mockTaskQueuer.getTasksAdded().get(0);
        Map<String, String> paramMap = taskAdded.getParamMap();

        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID));
        assertEquals(instructorAdded.getEmail(), paramMap.get(Const.ParamsNames.INSTRUCTOR_EMAIL));
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };

        ______TS("only instructors of the same course can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);

        // remove the newly added instructor
        logic.deleteInstructorCascade("idOfTypicalCourse1", "instructor@email.tmt");
    }
}
