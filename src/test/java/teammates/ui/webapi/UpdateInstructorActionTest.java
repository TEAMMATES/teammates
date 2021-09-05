package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link UpdateInstructorAction}.
 */
public class UpdateInstructorActionTest extends BaseActionTest<UpdateInstructorAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructorToEdit = typicalBundle.instructors.get("instructorNotDisplayedToStudent1");
        String instructorId = instructorToEdit.getGoogleId();
        String courseId = instructorToEdit.getCourseId();
        String instructorDisplayName = instructorToEdit.getDisplayedName();

        loginAsInstructor(instructorId);

        ______TS("Typical case: edit instructor successfully");

        final String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        String newInstructorName = "newName";
        String newInstructorEmail = "newEmail@email.com";
        String newInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR;

        InstructorCreateRequest reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, newInstructorRole,
                instructorDisplayName, instructorToEdit.isDisplayedToStudents());

        UpdateInstructorAction updateInstructorAction = getAction(reqBody, submissionParams);
        JsonResult actionOutput = getJsonResult(updateInstructorAction);

        InstructorData response = (InstructorData) actionOutput.getOutput();

        InstructorAttributes editedInstructor = logic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorName, editedInstructor.getName());
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, editedInstructor.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());
        assertFalse(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT));

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("Failure case: edit failed due to invalid parameters");

        String invalidEmail = "wrongEmail.com";
        reqBody = new InstructorCreateRequest(instructorId, instructorToEdit.getName(),
                invalidEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(reqBody, submissionParams);
        String expectedErrorMessage = FieldValidator.getInvalidityInfoForEmail(invalidEmail);
        assertEquals(expectedErrorMessage, ihrbe.getMessage());

        verifyNoTasksAdded();

        ______TS("Failure case: after editing instructor, no instructors are displayed");

        reqBody = new InstructorCreateRequest(instructorId, instructorToEdit.getName(),
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        InvalidOperationException ioe = verifyInvalidOperation(reqBody, submissionParams);
        assertEquals("At least one instructor must be displayed to students", ioe.getMessage());

        verifyNoTasksAdded();

        ______TS("Masquerade mode: edit instructor successfully");

        loginAsAdmin();

        newInstructorName = "newName2";
        newInstructorEmail = "newEmail2@email.com";

        reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        updateInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(updateInstructorAction);

        response = (InstructorData) actionOutput.getOutput();

        editedInstructor = logic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorEmail, editedInstructor.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());
        assertEquals(newInstructorName, editedInstructor.getName());
        assertEquals(newInstructorName, response.getName());

        //remove the new instructor entity that was created
        logic.deleteCourseCascade("icieat.courseId");

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("Unsuccessful case: test null course id parameter");

        String[] emptySubmissionParams = new String[0];
        InstructorCreateRequest newReqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        verifyHttpParameterFailure(newReqBody, emptySubmissionParams);

        verifyNoTasksAdded();

        ______TS("Unsuccessful case: test null instructor name parameter");

        InstructorCreateRequest nullNameReq = new InstructorCreateRequest(instructorId, null,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        verifyHttpRequestBodyFailure(nullNameReq, submissionParams);

        verifyNoTasksAdded();

        ______TS("Unsuccessful case: test null instructor email parameter");

        InstructorCreateRequest nullEmailReq = new InstructorCreateRequest(instructorId, newInstructorName,
                null, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        verifyHttpRequestBodyFailure(nullEmailReq, submissionParams);

        verifyNoTasksAdded();
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");

        ______TS("only instructors of the same course can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);
        ______TS("instructors of other courses cannot access");

        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }
}
