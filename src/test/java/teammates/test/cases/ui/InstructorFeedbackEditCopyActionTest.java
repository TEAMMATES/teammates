package teammates.test.cases.ui;

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
        System.out.println(new Gson().toJson(dataBundle));
        InstructorAttributes instructor = dataBundle.instructors.get("teammates.test.instructor2");
        String instructorId = instructor.googleId;
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("openSession");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("No parameters");
        verifyAssumptionFailure();
        
        ______TS("Courses not passed in");
        String[] params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                "newfsname", "valid name"
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
        
        
        ______TS("Successful case");
        params = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First Session",
                Const.ParamsNames.COURSE_ID, "CFeedbackEditCopyUiT.CS2104",
                "newfsname", "valid name",
                "coursesToCopyTo", "CFeedbackEditCopyUiT.CS1101",
                "coursesToCopyTo", "CFeedbackEditCopyUiT.CS2103"
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
