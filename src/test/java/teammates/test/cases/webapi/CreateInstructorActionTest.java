package teammates.test.cases.webapi;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.webapi.action.CreateInstructorAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.InstructorData;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.InstructorCreateRequest;

/**
 * SUT: {@link CreateInstructorAction}.
 */
public class CreateInstructorActionTest extends BaseActionTest<CreateInstructorAction> {

    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

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
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

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

        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        InstructorData response = (InstructorData) actionOutput.getOutput();

        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(newInstructorEmail, courseId));

        InstructorAttributes instructorAdded = instructorsLogic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.name);
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, instructorAdded.email);
        assertEquals(newInstructorEmail, response.getEmail());

        verifySpecifiedTasksAdded(createInstructorAction, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        TaskWrapper taskAdded = createInstructorAction.getTaskQueuer().getTasksAdded().get(0);

        assertEquals(courseId, taskAdded.getParamMap().get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(instructorAdded.email, reqBody.getEmail());
        assertEquals(instructorId, reqBody.getId());

        ______TS("Error: try to add an existing instructor");

        createInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(createInstructorAction);

        assertEquals(HttpStatus.SC_CONFLICT, actionOutput.getStatusCode());

        MessageOutput msg = (MessageOutput) actionOutput.getOutput();
        assertEquals("An instructor with the same email address already exists in the course.",
                msg.getMessage());

        verifyNoTasksAdded(createInstructorAction);

        ______TS("Error: try to add an instructor with invalid email");

        String newInvalidInstructorEmail = "ICIAAT.newInvalidInstructor.email.tmt";
        reqBody = new InstructorCreateRequest(null, newInstructorName, newInvalidInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        createInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(createInstructorAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        msg = (MessageOutput) actionOutput.getOutput();
        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, newInvalidInstructorEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                FieldValidator.EMAIL_MAX_LENGTH),
                msg.getMessage());

        verifyNoTasksAdded(createInstructorAction);

        ______TS("Masquerade mode: add an instructor");

        instructorsLogic.deleteInstructorCascade(courseId, newInstructorEmail);

        loginAsAdmin();
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        reqBody = new InstructorCreateRequest(instructorId, newInstructorName, newInstructorEmail,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        createInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(createInstructorAction);

        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        response = (InstructorData) actionOutput.getOutput();

        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(newInstructorEmail, courseId));

        instructorAdded = instructorsLogic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.name);
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, instructorAdded.email);
        assertEquals(newInstructorEmail, response.getEmail());

        verifySpecifiedTasksAdded(createInstructorAction, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        taskAdded = createInstructorAction.getTaskQueuer().getTasksAdded().get(0);
        Map<String, String[]> paramMap = taskAdded.getParamMap();

        assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        assertEquals(instructorAdded.email, paramMap.get(Const.ParamsNames.INSTRUCTOR_EMAIL)[0]);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };

        ______TS("only instructors of the same course can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, submissionParams);

        // remove the newly added instructor
        InstructorsLogic.inst().deleteInstructorCascade("idOfTypicalCourse1", "instructor@email.tmt");
    }
}
