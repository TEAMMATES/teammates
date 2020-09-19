package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.output.InstructorData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateInstructorAction;

/**
 * SUT: {@link UpdateInstructorAction}.
 */
public class UpdateInstructorActionTest extends BaseActionTest<UpdateInstructorAction> {
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

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
        String instructorId = instructorToEdit.googleId;
        String courseId = instructorToEdit.courseId;

        loginAsInstructor(instructorId);

        ______TS("Typical case: edit instructor successfully");

        final String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        String newInstructorName = "newName";
        String newInstructorEmail = "newEmail@email.com";

        InstructorCreateRequest reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, instructorToEdit.role,
                instructorToEdit.displayedName, instructorToEdit.isDisplayedToStudents);

        UpdateInstructorAction updateInstructorAction = getAction(reqBody, submissionParams);
        JsonResult actionOutput = getJsonResult(updateInstructorAction);
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        InstructorData response = (InstructorData) actionOutput.getOutput();

        InstructorAttributes editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorName, editedInstructor.name);
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, editedInstructor.email);
        assertEquals(newInstructorEmail, response.getEmail());
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));

        ______TS("Failure case: edit failed due to invalid parameters");

        String invalidEmail = "wrongEmail.com";
        reqBody = new InstructorCreateRequest(instructorId, instructorToEdit.name,
                invalidEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        updateInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(updateInstructorAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        MessageOutput msg = (MessageOutput) actionOutput.getOutput();
        String expectedErrorMessage = FieldValidator.getInvalidityInfoForEmail(invalidEmail);
        assertEquals(expectedErrorMessage, msg.getMessage());

        ______TS("Failure case: after editing instructor, no instructors are displayed");

        reqBody = new InstructorCreateRequest(instructorId, instructorToEdit.name,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        updateInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(updateInstructorAction);
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        msg = (MessageOutput) actionOutput.getOutput();
        assertEquals("At least one instructor must be displayed to students", msg.getMessage());

        ______TS("Masquerade mode: edit instructor successfully");

        loginAsAdmin();

        newInstructorName = "newName2";
        newInstructorEmail = "newEmail2@email.com";

        reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        updateInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(updateInstructorAction);

        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        response = (InstructorData) actionOutput.getOutput();

        editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorEmail, editedInstructor.email);
        assertEquals(newInstructorEmail, response.getEmail());
        assertEquals(newInstructorName, editedInstructor.name);
        assertEquals(newInstructorName, response.getName());

        //remove the new instructor entity that was created
        CoursesLogic.inst().deleteCourseCascade("icieat.courseId");

        ______TS("Unsuccessful case: test null course id parameter");

        final String[] emptySubmissionParams = new String[0];
        final InstructorCreateRequest newReqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        assertThrows(NullHttpParameterException.class, () -> {
            UpdateInstructorAction illegalAction = getAction(newReqBody, emptySubmissionParams);
            getJsonResult(illegalAction);
        });

        ______TS("Unsuccessful case: test null instructor name parameter");

        final InstructorCreateRequest nullNameReq = new InstructorCreateRequest(instructorId, null,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            UpdateInstructorAction illegalAction = getAction(nullNameReq, submissionParams);
            getJsonResult(illegalAction);
        });

        ______TS("Unsuccessful case: test null instructor email parameter");

        final InstructorCreateRequest nullEmailReq = new InstructorCreateRequest(instructorId, newInstructorName,
                null, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, true);

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            UpdateInstructorAction illegalAction = getAction(nullEmailReq, submissionParams);
            getJsonResult(illegalAction);
        });
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");

        ______TS("only instructors of the same course can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, submissionParams);
        ______TS("instructors of other courses cannot access");

        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }
}
