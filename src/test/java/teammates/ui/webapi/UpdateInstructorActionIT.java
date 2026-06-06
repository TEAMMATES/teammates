package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link UpdateInstructorAction}.
 */
public class UpdateInstructorActionIT extends BaseActionIT<UpdateInstructorAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute() {
        Instructor instructorToEdit = typicalBundle.instructors.get("instructor2OfCourse1");
        String instructorDisplayName = instructorToEdit.getDisplayName();

        loginAsInstructor(instructorToEdit.getGoogleId());

        ______TS("Typical case: edit instructor successfully");

        final String[] submissionParams = new String[0];

        String newInstructorName = "newName";
        String newInstructorEmail = "newemail@email.com";
        String newInstructorRole = Const.InstructorPermissionRoleNames.COOWNER;

        InstructorUpdateRequest reqBody = new InstructorUpdateRequest(instructorToEdit.getId(), newInstructorName,
                newInstructorEmail, newInstructorRole,
                instructorDisplayName, false);

        UpdateInstructorAction updateInstructorAction = getAction(reqBody, submissionParams);
        JsonResult actionOutput = getJsonResult(updateInstructorAction);

        InstructorData response = (InstructorData) actionOutput.getOutput();

        Instructor editedInstructor = inTransaction(() -> logic.getInstructor(instructorToEdit.getId()));
        assertEquals(newInstructorName, editedInstructor.getName());
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, editedInstructor.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());
        assertFalse(editedInstructor.isDisplayedToStudents());
        assertTrue(logic.hasInstructorPermissions(editedInstructor,
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(logic.hasInstructorPermissions(editedInstructor,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(logic.hasInstructorPermissions(editedInstructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(logic.hasInstructorPermissions(editedInstructor,
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));

        ______TS("Failure case: edit failed due to invalid parameters");

        String invalidEmail = "wrongemail.com";
        reqBody = new InstructorUpdateRequest(instructorToEdit.getId(), instructorToEdit.getName(),
                invalidEmail, Const.InstructorPermissionRoleNames.COOWNER,
                instructorDisplayName, true);

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(reqBody, submissionParams);
        String expectedErrorMessage = FieldValidator.getInvalidityInfoForEmail(invalidEmail);
        assertEquals(expectedErrorMessage, ihrbe.getMessage());

        verifyNoTasksAdded();

        ______TS("Failure case: after editing instructor, no instructors are displayed");

        final Instructor instructorWithNoVisiblePeers = typicalBundle.instructors.get("instructor1OfCourse3");

        loginAsInstructor(instructorWithNoVisiblePeers.getGoogleId());

        reqBody = new InstructorUpdateRequest(instructorWithNoVisiblePeers.getId(),
                instructorWithNoVisiblePeers.getName(), instructorWithNoVisiblePeers.getEmail(),
                Const.InstructorPermissionRoleNames.COOWNER,
                null, false);

        InvalidOperationException ioe = verifyInvalidOperation(reqBody);

        assertEquals("At least one instructor must be displayed to students", ioe.getMessage());

        verifyNoTasksAdded();

        ______TS("Masquerade mode: edit instructor successfully");

        loginAsAdmin();

        newInstructorName = "newName2";
        newInstructorEmail = "newemail2@email.com";

        reqBody = new InstructorUpdateRequest(instructorToEdit.getId(), newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.COOWNER,
                instructorDisplayName, true);

        updateInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(updateInstructorAction);

        response = (InstructorData) actionOutput.getOutput();

        editedInstructor = inTransaction(() -> logic.getInstructor(instructorToEdit.getId()));
        assertEquals(newInstructorEmail, editedInstructor.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());
        assertEquals(newInstructorName, editedInstructor.getName());
        assertEquals(newInstructorName, response.getName());

        //remove the new instructor entity that was created
        inTransaction(() -> logic.deleteCourse("icieat.courseId"));

        ______TS("Unsuccessful case: test null course id parameter");

        String[] emptySubmissionParams = new String[0];
        InstructorUpdateRequest newReqBody = new InstructorUpdateRequest(null, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.COOWNER,
                instructorDisplayName, true);

        verifyHttpRequestBodyFailure(newReqBody, emptySubmissionParams);

        verifyNoTasksAdded();

        ______TS("Unsuccessful case: test null instructor name parameter");

        InstructorUpdateRequest nullNameReq = new InstructorUpdateRequest(
                null, null, newInstructorEmail,
                Const.InstructorPermissionRoleNames.COOWNER, instructorDisplayName, true);

        verifyHttpRequestBodyFailure(nullNameReq, submissionParams);

        verifyNoTasksAdded();

        ______TS("Unsuccessful case: test null instructor email parameter");

        InstructorUpdateRequest nullEmailReq = new InstructorUpdateRequest(
                null, newInstructorName, null,
                Const.InstructorPermissionRoleNames.COOWNER, instructorDisplayName, true);

        verifyHttpRequestBodyFailure(nullEmailReq, submissionParams);

        verifyNoTasksAdded();
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor2OfCourse1");

        ______TS("only instructors of the same course can access");

        InstructorUpdateRequest reqBody = new InstructorUpdateRequest(instructor.getId(),
                instructor.getName(), instructor.getEmail(), Const.InstructorPermissionRoleNames.COOWNER,
                instructor.getDisplayName(), true);

        Instructor privilegedInstructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(privilegedInstructor.getGoogleId());
        final UpdateInstructorAction sameCourseAction = getAction(reqBody);
                inTransaction(() -> {
                        assertDoesNotThrow(sameCourseAction::checkAccessControl);
                        return null;
                });

                ______TS("instructors without correct privilege cannot access");

                Instructor noPrivilegeInstructor = typicalBundle.instructors.get("instructor2OfCourse1");
                loginAsInstructor(noPrivilegeInstructor.getGoogleId());
                final UpdateInstructorAction noPrivilegeAction = getAction(reqBody);
                inTransaction(() -> {
                        assertThrows(UnauthorizedAccessException.class, noPrivilegeAction::checkAccessControl);
                        return null;
                });
    }
}
