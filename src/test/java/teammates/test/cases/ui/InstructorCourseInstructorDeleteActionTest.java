package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseInstructorDeleteActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes loginInstructor = dataBundle.instructors.get("instructor1OfCourse1");
        String loginInstructorId = loginInstructor.googleId;
        String courseId = loginInstructor.courseId;
        String adminUserId = "admin.user";

        gaeSimulation.loginAsInstructor(loginInstructorId);
        
        ______TS("Typical case: Delete other instructor successfully, redirect back to edit page");
        
        InstructorAttributes instructorToDelete = dataBundle.instructors.get("instructor2OfCourse1");
        String instructorEmailToDelete = instructorToDelete.email;
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete
        };
        
        Action deleteAction = getAction(submissionParams);
        RedirectResult redirectResult = (RedirectResult) deleteAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE
                         + "?error=false&user=idOfInstructor1OfCourse1&courseid=idOfTypicalCourse1",
                     redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, redirectResult.getStatusMessage());

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructorEmailToDelete, courseId));
        
        String expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorEmailToDelete + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
        AssertHelper.assertContains(expectedLogSegment, deleteAction.getLogMessage());

        ______TS("Success: delete own instructor role from course, redirect back to courses page");
        
        instructorEmailToDelete = loginInstructor.email;
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete
        };
        
        deleteAction = getAction(submissionParams);
        redirectResult = (RedirectResult) deleteAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE + "?error=false&user=idOfInstructor1OfCourse1",
                        redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, redirectResult.getStatusMessage());

        assertFalse(instructorsLogic.isGoogleIdOfInstructorOfCourse(loginInstructor.googleId, courseId));
        
        expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorEmailToDelete + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
        AssertHelper.assertContains(expectedLogSegment, deleteAction.getLogMessage());
        
        ______TS("Masquerade mode: delete instructor failed due to last instructor in course");

        instructorToDelete = dataBundle.instructors.get("instructor4");
        instructorEmailToDelete = instructorToDelete.email;
        courseId = instructorToDelete.courseId;
        
        gaeSimulation.loginAsAdmin(adminUserId);
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete
        };
        
        deleteAction = getAction(addUserIdToParams(instructorToDelete.googleId, submissionParams));
        redirectResult = (RedirectResult) deleteAction.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE
                             + "?error=true&user=idOfInstructor4&courseid=idOfCourseNoEvals",
                     redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED, redirectResult.getStatusMessage());

        assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorToDelete.googleId, courseId));
        
        expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorEmailToDelete + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> could not be deleted "
                + "as there is only one instructor left to be able to modify instructors.<br>";
        AssertHelper.assertContains(expectedLogSegment, deleteAction.getLogMessage());
    }
    
    private Action getAction(String... params) {
        return gaeSimulation.getActionObject(uri, params);
    }
}
