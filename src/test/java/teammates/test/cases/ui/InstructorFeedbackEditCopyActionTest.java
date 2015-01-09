package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.Gson;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackEditCopyAction;
import teammates.ui.controller.InstructorFeedbackEditCopyPageAction;
import teammates.ui.controller.InstructorFeedbackEditCopyPageData;
import teammates.ui.controller.RedirectResult;


public class InstructorFeedbackEditCopyActionTest extends
        BaseActionTest {
    private static DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        dataBundle = loadDataBundle("/InstructorFeedbackEditCopyUiTest.json");
        removeAndRestoreDatastoreFromJson("/InstructorFeedbackEditCopyUiTest.json");
        
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor = dataBundle.instructors.get("teammates.test.instructor2");
        String instructorId = instructor.googleId;
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("openSession");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Failure case: No parameters");
        verifyAssumptionFailure();
        
        ______TS("Failure case: Courses not passed in");
        String[] params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name"
        };
        
        InstructorFeedbackEditCopyAction a = getAction(params);
        RedirectResult rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?error=true"
                        + "&user="
                        + instructor.googleId
                        + "&courseid="
                        + instructor.courseId
                        + "&fsname=First+Session",
                rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, rr.getStatusMessage());
        
        ______TS("Failure case: course already has feedback session with same name");
        params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, "CFeedbackEditCopyUiT.CS2104",
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COPIED_COURSES_ID, "CFeedbackEditCopyUiT.CS2104",
                Const.ParamsNames.COPIED_COURSES_ID, "CFeedbackEditCopyUiT.CS2103"
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?error=true"
                        + "&user="
                        + instructor.googleId
                        + "&courseid="
                        + instructor.courseId
                        + "&fsname=First+Session",
                rr.getDestinationWithParams());
        
        assertEquals("A feedback session with the name \"First Session\" already exists in the course(s) CFeedbackEditCopyUiT.CS2104.", rr.getStatusMessage());
        
        // Check that the feedback session is not copied to the other course as well
        assertNull(BackDoor.getFeedbackSession("CFeedbackEditCopyUiT.CS2103", "First Session"));
        
        ______TS("Successful case");
        params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, "CFeedbackEditCopyUiT.CS2104",
                Const.ParamsNames.COPIED_FEEDBACK_SESSION_NAME, "valid name",
                Const.ParamsNames.COPIED_COURSES_ID, "CFeedbackEditCopyUiT.CS1101",
                Const.ParamsNames.COPIED_COURSES_ID, "CFeedbackEditCopyUiT.CS2103"
        };
        
        a = getAction(params);
        rr = (RedirectResult) a.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE
                        + "?error=false"
                        + "&user="
                        + instructor.googleId,
                rr.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_COPIED, rr.getStatusMessage());
    }
    
    private InstructorFeedbackEditCopyAction getAction(String... params)
            throws Exception {

        return (InstructorFeedbackEditCopyAction) (gaeSimulation
                .getActionObject(uri, params));

    }
}
