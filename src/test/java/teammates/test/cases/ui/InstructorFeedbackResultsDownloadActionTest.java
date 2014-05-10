package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.FileDownloadResult;
import teammates.ui.controller.InstructorFeedbackResultsDownloadAction;

public class InstructorFeedbackResultsDownloadActionTest extends BaseActionTest {
    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsNormal = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithNullCourseId = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithNullFeedbackSessionName = {
                Const.ParamsNames.COURSE_ID, session.courseId
        };
        
        ______TS("Typical successful case: results downloadable");
        
        InstructorFeedbackResultsDownloadAction action = getAction(paramsNormal);
        FileDownloadResult result = (FileDownloadResult) action.executeAndPostProcess();
        
        String expectedDestination = "filedownload?" +
                "error=false" +
                "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);
        
        String expectedFileName = session.courseId + "_" + session.feedbackSessionName;
        String expectedFileContent = getExpectedFileContentForSession1InCourse1();
        assertEquals(expectedFileName, result.getFileName());
        assertEquals(expectedFileContent, result.getFileContent());
        
        ______TS("Unsuccessful case 1: params with null course id");
        
        verifyAssumptionFailure(paramsWithNullCourseId);
        
        ______TS("Unsuccessful case 2: params with null feedback session name");
        
        verifyAssumptionFailure(paramsWithNullFeedbackSessionName);
    }
    
    private String getExpectedFileContentForSession1InCourse1(){
        return  "Course,\"idOfTypicalCourse1\"\n" +
                "Session Name,\"First feedback session\"\n\n\n" +
                "Question 1,\"What is the best selling point of your product?\"\n\n" +
                "Team,Giver,Recipient's Team,Recipient,Feedback\n" + 
                "\"Team 1.1\",\"student1 In Course1\",\"Team 1.1\",\"student1 In Course1\",\"Student 1 self feedback.\"\n" +
                "\"Team 1.1\",\"student2 In Course1\",\"Team 1.1\",\"student2 In Course1\",\"I'm cool'\"\n\n\n" +
                "Question 2,\"Rate 1 other student's product\"\n\n" +
                "Team,Giver,Recipient's Team,Recipient,Feedback\n" +
                "\"Team 1.1\",\"student2 In Course1\",\"Team 1.1\",\"student1 In Course1\",\"Response from student 2 to student 1.\"\n" +
                "\"Team 1.1\",\"student1 In Course1\",\"Team 1.1\",\"student2 In Course1\",\"Response from student 1 to student 2.\"\n" +
                "\"Team 1.1\",\"student3 In Course1\",\"Team 1.1\",\"student2 In Course1\",\"Response from student 3 \"\"to\"\" student 2.\r\n" +
                "Multiline test.\"\n\n\n" +
                "Question 3,\"My comments on the class\"\n\n" +
                "Team,Giver,Recipient's Team,Recipient,Feedback\n" +
                "\"Instructors\",\"Instructor1 Course1\",\"\",\"-\",\"Good work, keep it up!\"\n\n\n";
    }
    
    private InstructorFeedbackResultsDownloadAction getAction(String[] params){
        return (InstructorFeedbackResultsDownloadAction) gaeSimulation.getActionObject(uri, params);
    }
}
