package teammates.test.cases.ui;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackPreviewAsInstructorAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackPreviewAsInstructorActionTest extends
        BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse1");
        String idOfInstructor1 = instructor1.googleId;
        
        gaeSimulation.loginAsInstructor(idOfInstructor1);
        
        ______TS("typical success case");
        
        String feedbackSessionName = "First feedback session";
        String courseId = "idOfTypicalCourse1";
        String previewAsEmail = instructor2.email;
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };
        
        InstructorFeedbackPreviewAsInstructorAction paia = getAction(submissionParams);
        ShowPageResult showPageResult = (ShowPageResult) paia.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT 
                + "?error=false"
                + "&user="+ idOfInstructor1
                ,showPageResult.getDestinationWithParams());
        
        assertEquals("", showPageResult.getStatusMessage());
        
        assertEquals("TEAMMATESLOG|||instructorFeedbackPreviewAsInstructor|||instructorFeedbackPreviewAsInstructor"
                + "|||true|||Instructor|||Instructor 1 of Course 1" 
                + "|||" + idOfInstructor1 + "|||instr1@course1.com|||"
                + "Preview feedback session as instructor (" + instructor2.email + ")<br>"
                + "Session Name: First feedback session<br>Course ID: " + instructor1.courseId
                + "|||/page/instructorFeedbackPreviewAsInstructor"
                , paia.getLogMessage());
        
        ______TS("failure: non-existent previewas email");
        previewAsEmail = "non-existentEmail@course13212.com";
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };
        
        try {
            paia = getAction(submissionParams);
            showPageResult = (ShowPageResult) paia.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Instructor Email "
                            + previewAsEmail + " does not exist in " + courseId
                            + ".", 
                        edne.getMessage());
        }
    }
    
    private InstructorFeedbackPreviewAsInstructorAction getAction(String... params) throws Exception{
        return (InstructorFeedbackPreviewAsInstructorAction) gaeSimulation.getActionObject(uri, params);
    }
}
