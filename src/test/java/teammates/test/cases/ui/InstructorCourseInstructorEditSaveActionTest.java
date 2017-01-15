package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseInstructorEditSaveActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructorToEdit = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructorToEdit.googleId;
        String courseId = instructorToEdit.courseId;
        
        String adminUserId = "admin.user";
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Typical case: edit instructor successfully");
        
        String newInstructorName = "newName";
        String newInstructorEmail = "newEmail@email.com";
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.INSTRUCTOR_NAME, newInstructorName,
                Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail,
                
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER
        };
        Action saveAction = getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                    Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                    redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, newInstructorName),
                     redirectResult.getStatusMessage());
        
        InstructorAttributes editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorName, editedInstructor.name);
        assertEquals(newInstructorEmail, editedInstructor.email);
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        
        String expectedLogSegment = "Instructor <span class=\"bold\"> " + newInstructorName + "</span>"
                + " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                + "New Name: " + newInstructorName + "<br>New Email: " + newInstructorEmail;
        AssertHelper.assertContains(expectedLogSegment, saveAction.getLogMessage());
       
        
        ______TS("Failure case: edit failed due to invalid parameters");
        
        String invalidEmail = "wrongEmail.com";
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.INSTRUCTOR_NAME, instructorToEdit.name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail,
                
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true"
        };
        
        saveAction = getAction(submissionParams);
        redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        String expectedErrorMessage = new FieldValidator().getInvalidityInfoForEmail(invalidEmail);
        assertEquals(expectedErrorMessage, redirectResult.getStatusMessage());
        
        AssertHelper.assertContains(expectedErrorMessage, saveAction.getLogMessage());
        
        ______TS("Masquerade mode: edit instructor successfully");
        
        gaeSimulation.loginAsAdmin(adminUserId);
        
        newInstructorName = "newName2";
        newInstructorEmail = "newEmail2@email.com";
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.INSTRUCTOR_NAME, newInstructorName,
                Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail,
                
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true"
        };
        
        saveAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, newInstructorName),
                     redirectResult.getStatusMessage());
        
        editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorEmail, editedInstructor.email);
        assertEquals(newInstructorName, editedInstructor.name);
        
        expectedLogSegment = "Instructor <span class=\"bold\"> " + newInstructorName + "</span>"
                + " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                + "New Name: " + newInstructorName + "<br>New Email: " + newInstructorEmail;
        AssertHelper.assertContains(expectedLogSegment, saveAction.getLogMessage());
        
        //remove the new instructor entity that was created
        CoursesLogic.inst().deleteCourseCascade("icieat.courseId");
        
        
        ______TS("Unsuccessful case: test null course id parameter");
        submissionParams = new String[]{
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.INSTRUCTOR_NAME, newInstructorName,
                Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail,
                
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true"
        };
        
        try {
            saveAction = getAction(submissionParams);
            redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }
        
        
        ______TS("Unsuccessful case: test null instructor name parameter");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail,
                
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true"
        };
        
        try {
            saveAction = getAction(submissionParams);
            redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.INSTRUCTOR_NAME), e.getMessage());
        }
        
        
        ______TS("Unsuccessful case: test null instructor email parameter");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.INSTRUCTOR_NAME, newInstructorName,
                
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true"
        };
        
        try {
            saveAction = getAction(submissionParams);
            redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail), e.getMessage());
        }
    }
    
    private Action getAction(String... parameters) {
        return gaeSimulation.getActionObject(uri, parameters);
    }
}
