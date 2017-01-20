package teammates.test.cases.ui;

import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TaskWrapper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorCourseInstructorAddAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseInstructorAddActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        String adminUserId = "admin.user";
         
        ______TS("Typical case: add an instructor successfully");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        String newInstructorName = "New Instructor Name";
        String newInstructorEmail = "ICIAAT.newInstructor@email.tmt";
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
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
        
        Action addAction = getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) addAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                    newInstructorName, newInstructorEmail), redirectResult.getStatusMessage());
        
        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(newInstructorEmail, courseId));
        
        InstructorAttributes instructorAdded = instructorsLogic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.name);
        assertEquals(newInstructorEmail, instructorAdded.email);
        
        String expectedLogSegment = "New instructor (<span class=\"bold\"> " + newInstructorEmail + "</span>)"
                + " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
        AssertHelper.assertContains(expectedLogSegment, addAction.getLogMessage());
        
        verifySpecifiedTasksAdded(addAction, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
        
        TaskWrapper taskAdded = addAction.getTaskQueuer().getTasksAdded().get(0);
        Map<String, String[]> paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID)[0]);
        assertEquals(instructorAdded.email, paramMap.get(ParamsNames.INSTRUCTOR_EMAIL)[0]);
        
        ______TS("Error: try to add an existing instructor");
        
        addAction = getAction(submissionParams);
        redirectResult = (RedirectResult) addAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS, redirectResult.getStatusMessage());

        expectedLogSegment = "TEAMMATESLOG|||instructorCourseInstructorAdd|||instructorCourseInstructorAdd"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt"
                + "|||Servlet Action Failure : Trying to create a Instructor that exists: "
                + "idOfTypicalCourse1/ICIAAT.newInstructor@email.tmt"
                + "|||/page/instructorCourseInstructorAdd";
        AssertHelper.assertLogMessageEquals(expectedLogSegment, addAction.getLogMessage());
        
        verifyNoTasksAdded(addAction);
        
        ______TS("Error: try to add an instructor with invalid email");
        String newInvalidInstructorEmail = "ICIAAT.newInvalidInstructor.email.tmt";
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_NAME, newInstructorName,
                Const.ParamsNames.INSTRUCTOR_EMAIL, newInvalidInstructorEmail,
                
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME,
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER
        };
        
        addAction = getAction(submissionParams);
        redirectResult = (RedirectResult) addAction.executeAndPostProcess();
        
        AssertHelper.assertContains(
                Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, newInvalidInstructorEmail,
                         FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                         FieldValidator.EMAIL_MAX_LENGTH),
                     redirectResult.getStatusMessage());
            
        expectedLogSegment = "TEAMMATESLOG|||instructorCourseInstructorAdd|||instructorCourseInstructorAdd"
               + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt"
               + "|||Servlet Action Failure : "
               + getPopulatedErrorMessage(
                     FieldValidator.EMAIL_ERROR_MESSAGE, newInvalidInstructorEmail,
                     FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                     FieldValidator.EMAIL_MAX_LENGTH)
               + "|||/page/instructorCourseInstructorAdd";
        AssertHelper.assertLogMessageEquals(expectedLogSegment, addAction.getLogMessage());
        
        verifyNoTasksAdded(addAction);
        
        ______TS("Masquerade mode: add an instructor");
        
        instructorsLogic.deleteInstructorCascade(courseId, newInstructorEmail);

        gaeSimulation.loginAsAdmin(adminUserId);
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
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
        addAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = (RedirectResult) addAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                newInstructorName, newInstructorEmail), redirectResult.getStatusMessage());
        
        assertTrue(instructorsLogic.isEmailOfInstructorOfCourse(newInstructorEmail, courseId));
        
        instructorAdded = instructorsLogic.getInstructorForEmail(courseId, newInstructorEmail);
        assertEquals(newInstructorName, instructorAdded.name);
        assertEquals(newInstructorEmail, instructorAdded.email);
        
        expectedLogSegment = "New instructor (<span class=\"bold\"> " + newInstructorEmail + "</span>)"
                + " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
        AssertHelper.assertContains(expectedLogSegment, addAction.getLogMessage());
        
        verifySpecifiedTasksAdded(addAction, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);
        
        taskAdded = addAction.getTaskQueuer().getTasksAdded().get(0);
        paramMap = taskAdded.getParamMap();
        assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID)[0]);
        assertEquals(instructorAdded.email, paramMap.get(ParamsNames.INSTRUCTOR_EMAIL)[0]);
        
    }
    
    private InstructorCourseInstructorAddAction getAction(String... parameters) {
        return (InstructorCourseInstructorAddAction) gaeSimulation.getActionObject(uri, parameters);
    }

}
