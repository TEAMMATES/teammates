package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.ui.controller.FileDownloadResult;
import teammates.ui.controller.InstructorFeedbackResultsDownloadAction;

public class InstructorFeedbackResultsDownloadActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsNormal = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsNormalWithinSection = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.SECTION_NAME, "Section 1"
        };
        String[] paramsWithNullCourseId = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithNullFeedbackSessionName = {
                Const.ParamsNames.COURSE_ID, session.courseId
        };

        ______TS("Typical case: results downloadable");

        InstructorFeedbackResultsDownloadAction action = getAction(paramsNormal);
        FileDownloadResult result = (FileDownloadResult) action.executeAndPostProcess();

        String expectedDestination = "filedownload?" + "error=false" + "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        String expectedFileName = session.courseId + "_" + session.feedbackSessionName;
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1(result.getFileContent(), session);

        ______TS("Typical successful case: student last name displayed properly after being specified with braces");

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        student1InCourse1.name = "new name {new last name}";
        StudentsLogic studentsLogic = StudentsLogic.inst();
        studentsLogic.updateStudentCascade(student1InCourse1.email, student1InCourse1);

        action = getAction(paramsNormal);
        result = (FileDownloadResult) action.executeAndPostProcess();

        expectedDestination = "filedownload?" + "error=false" + "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        expectedFileName = session.courseId + "_" + session.feedbackSessionName;
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1WithNewLastName(result.getFileContent(), session);

        removeAndRestoreTypicalDataInDatastore();

        ______TS("Typical case: results within section downloadable");

        action = getAction(paramsNormalWithinSection);
        result = (FileDownloadResult) action.executeAndPostProcess();

        expectedDestination = "filedownload?" + "error=false" + "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);

        expectedFileName = session.courseId + "_" + session.feedbackSessionName + "_Section 1";
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1WithinSection1(result.getFileContent(), session);

        ______TS("Failure case: params with null course id");

        try {
            action = getAction(paramsWithNullCourseId);
            result = (FileDownloadResult) action.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.COURSE_ID),
                         e.getMessage());
        }

        ______TS("Failure case: params with null feedback session name");

        try {
            action = getAction(paramsWithNullFeedbackSessionName);
            result = (FileDownloadResult) action.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }
    }

    private void verifyFileContentForSession1InCourse1(String fileContent,
                                                       FeedbackSessionAttributes session) {
        /* This is what fileContent should look like:
        ==================================
        Course,idOfTypicalCourse1
        Session Name,First feedback session
        
        
        Question 1,"What is the best selling point of your product?"
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1","Course1","Team 1.1","student1 In Course1","Course1","Student 1 self feedback."
        "Team 1.1","student2 In Course1","Course1","Team 1.1","student2 In Course1","Course1","I'm cool'"
        ...
        ==================================
        full testing of file content is 
        in FeedbackSessionsLogicTest.testGetFeedbackSessionResultsSummaryAsCsv()
        */
        System.out.println(fileContent);

        String[] exportLines = fileContent.split(Const.EOL);
        assertEquals("Course,\"" + session.courseId + "\"",
                     exportLines[0]);
        assertEquals("Session Name,\"" + session.feedbackSessionName + "\"",
                     exportLines[1]);
        assertEquals("", exportLines[2]);
        assertEquals("", exportLines[3]);
        assertEquals("Question 1,\"What is the best selling point of your product?\"",
                     exportLines[4]);
        assertEquals("", exportLines[5]);
        assertEquals("Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                     exportLines[6]);
        assertEquals("\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\"",
                     exportLines[7]);
        assertEquals("\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\"",
                     exportLines[8]);
    }    

    private void verifyFileContentForSession1InCourse1WithNewLastName(String fileContent,
                                                                      FeedbackSessionAttributes session) {
        /* This is what fileContent should look like:
        ==================================
        Course,idOfTypicalCourse1
        Session Name,First feedback session
        
        
        Question 1,"What is the best selling point of your product?"
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","new name","new last name","student1InCourse1@gmail.tmt","Team 1.1","new name","new last name","student1InCourse1@gmail.tmt","Student 1 self feedback."
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","I'm cool'"
        ...
        ==================================
        full testing of file content is 
        in FeedbackSessionsLogicTest.testGetFeedbackSessionResultsSummaryAsCsv()
        */
        System.out.println(fileContent);
        
        String[] exportLines = fileContent.split(Const.EOL);
        assertEquals("Course,\"" + session.courseId + "\"", 
                     exportLines[0]);
        assertEquals("Session Name,\"" + session.feedbackSessionName + "\"", 
                     exportLines[1]);
        assertEquals("", exportLines[2]);
        assertEquals("", exportLines[3]);
        assertEquals("Question 1,\"What is the best selling point of your product?\"",
                     exportLines[4]);
        assertEquals("", exportLines[5]);
        assertEquals("Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                     exportLines[6]);
        assertEquals("\"Team 1.1\",\"new name new last name\",\"new last name\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"new name new last name\",\"new last name\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\"",
                     exportLines[7]);
        assertEquals("\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\"",
                     exportLines[8]);
    }

    private void verifyFileContentForSession1InCourse1WithinSection1(String fileContent,
                                                                     FeedbackSessionAttributes session) {
        /* This is what fileContent should look like:
        ==================================
        Course,idOfTypicalCourse1
        Session Name,First feedback session
        Section Name,Section 1
        
        
        Question 1,"What is the best selling point of your product?"
        
        Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback
        "Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Team 1.1","student1 In Course1","Course1","student1InCourse1@gmail.tmt","Student 1 self feedback."
        "Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","Team 1.1","student2 In Course1","Course1","student2InCourse1@gmail.tmt","I'm cool'"
        ...
        ==================================
        full testing of file content is 
        in FeedbackSessionsLogicTest.testGetFeedbackSessionResultsSummaryAsCsv()
        */
        System.out.println(fileContent);
        
        String[] exportLines = fileContent.split(Const.EOL);
        assertEquals("Course,\"" + session.courseId + "\"", 
                     exportLines[0]);
        assertEquals("Session Name,\"" + session.feedbackSessionName + "\"", 
                     exportLines[1]);
        assertEquals("Section Name,\"Section 1\"",
                     exportLines[2]);
        assertEquals("", exportLines[3]);
        assertEquals("", exportLines[4]);
        assertEquals("Question 1,\"What is the best selling point of your product?\"",
                     exportLines[5]);
        assertEquals("", exportLines[6]);
        assertEquals("Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                     exportLines[7]);
        assertEquals("\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\"",
                     exportLines[8]);
        assertEquals("\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\"",
                     exportLines[9]);
    }

    private InstructorFeedbackResultsDownloadAction getAction(String[] params) {
        return (InstructorFeedbackResultsDownloadAction) gaeSimulation.getActionObject(uri, params);
    }

}
