package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackEditPageAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackEditPageActionTest extends BaseActionTest {
    
    DataBundle dataBundle;
        
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        
        // declare all variables to be used
        String expectedLogMessage = "";
        FeedbackSessionAttributes feedbackSessionAttributes;
        String[] submissionParams;
        InstructorFeedbackEditPageAction instructorFeedbackEditPageAction;
        ShowPageResult showPageResult;
        
        ______TS("typical success case");
        
        feedbackSessionAttributes = dataBundle.feedbackSessions.get("session1InCourse1");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.feedbackSessionName
        };
        
        instructorFeedbackEditPageAction = getAction(submissionParams);
        showPageResult = (ShowPageResult) instructorFeedbackEditPageAction.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_EDIT
                + "?error=false"
                + "&user="
                + instructor1OfCourse1.googleId,
                showPageResult.getDestinationWithParams());
        
        expectedLogMessage = 
                "TEAMMATESLOG|||instructorFeedbackEditPage|||instructorFeedbackEditPage|||true|||" +
                "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||" +
                "instr1@course1.com|||instructorFeedbackEdit Page Load<br>Editing information for Feedback Session" +
                " <span class=\"bold\">[" + feedbackSessionAttributes.feedbackSessionName + "]</span>" +
                "in Course: <span class=\"bold\">[idOfTypicalCourse1]</span>" +
                "|||/page/instructorFeedbackEditPage";
        
        assertEquals(expectedLogMessage, instructorFeedbackEditPageAction.getLogMessage());
        
        ______TS("failure 1: non-existent feedback session");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName for Session123"
        };
        
        instructorFeedbackEditPageAction = getAction(submissionParams);
        try {
            showPageResult = (ShowPageResult) instructorFeedbackEditPageAction.executeAndPostProcess();
            signalFailureToDetectException();
        } catch(UnauthorizedAccessException uae) {
            assertEquals("Trying to access system using a non-existent feedback session entity",
                    uae.getMessage());
        }
    }
    
    private InstructorFeedbackEditPageAction getAction (String... params) throws Exception {
        return (InstructorFeedbackEditPageAction) gaeSimulation.getActionObject(uri, params);
    }
}
