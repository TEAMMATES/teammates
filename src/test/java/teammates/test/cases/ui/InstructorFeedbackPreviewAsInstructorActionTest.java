package teammates.test.cases.ui;

import static org.junit.Assert.assertEquals;

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
    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, instructor.email
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        String idOfInstr1OfCourse1 = "idOfInstructor1OfCourse1";
        
        gaeSimulation.loginAsInstructor(idOfInstr1OfCourse1);
        
        ______TS("typical success case");
        
        String feedbackSessionName = "First feedback session";
        String courseId = "idOfTypicalCourse1";
        String previewAsEmail = "instructor2@course1.com";
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, previewAsEmail
        };
        
        InstructorFeedbackPreviewAsInstructorAction paia = getAction(submissionParams);
        ShowPageResult showPageResult = (ShowPageResult) paia.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT 
                + "?error=false"
                + "&user="+ idOfInstr1OfCourse1
                ,showPageResult.getDestinationWithParams());
        
        assertEquals("TEAMMATESLOG|||instructorFeedbackPreviewAsInstructor|||instructorFeedbackPreviewAsInstructor"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                + "Preview feedback session as instructor (instructor2@course1.com)<br>"
                + "Session Name: First feedback session<br>Course ID: idOfTypicalCourse1|||"
                + "/page/instructorFeedbackPreviewAsInstructor"
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
