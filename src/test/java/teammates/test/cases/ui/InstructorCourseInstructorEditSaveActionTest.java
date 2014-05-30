package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.FieldValidator.FieldType;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseInstructorEditSaveActionTest extends BaseActionTest {

    DataBundle dataBundle;
    InstructorsLogic instructorsLogic;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
        instructorsLogic = InstructorsLogic.inst();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructor.googleId,
                Const.ParamsNames.INSTRUCTOR_NAME, instructor.name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, "newEmail@email.com"
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        //TODO: find a way to test status message from session
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
                Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail
        };
        Action saveAction = getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                    Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                    redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, redirectResult.getStatusMessage());
        
        InstructorAttributes editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorName, editedInstructor.name);
        assertEquals(newInstructorEmail, editedInstructor.email);
        
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
                Const.ParamsNames.INSTRUCTOR_EMAIL, invalidEmail
        };
        
        saveAction = getAction(submissionParams);
        redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                redirectResult.getDestinationWithParams());
        assertEquals(true, redirectResult.isError);
        String expectedErrorMessage = (new FieldValidator()).getInvalidityInfo(FieldType.EMAIL, invalidEmail);
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
                Const.ParamsNames.INSTRUCTOR_EMAIL, newInstructorEmail
        };
        
        saveAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = (RedirectResult) saveAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, redirectResult.getStatusMessage());
        
        editedInstructor = instructorsLogic.getInstructorForGoogleId(courseId, instructorId);
        assertEquals(newInstructorEmail, editedInstructor.email);
        assertEquals(newInstructorName, editedInstructor.name);
        
        expectedLogSegment = "Instructor <span class=\"bold\"> " + newInstructorName + "</span>"
                + " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                + "New Name: " + newInstructorName + "<br>New Email: " + newInstructorEmail;
        AssertHelper.assertContains(expectedLogSegment, saveAction.getLogMessage());
        
    }
    
    private Action getAction(String... parameters) throws Exception {
        return gaeSimulation.getActionObject(uri, parameters);
    }
}
