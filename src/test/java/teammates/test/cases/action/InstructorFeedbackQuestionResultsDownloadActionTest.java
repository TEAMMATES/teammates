package teammates.test.cases.action;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.ui.controller.FileDownloadResult;
import teammates.ui.controller.InstructorFeedbackQuestionResultsDownloadAction;

public class InstructorFeedbackQuestionResultsDownloadActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_RESULTS_DOWNLOAD;
    }
    
    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsQuestion2 = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2"
        };
        String[] paramsQuestion1WithinSection = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1"
        };
        String[] paramsWithNullCourseId = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "3"
        };
        String[] paramsWithNullFeedbackSessionName = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "4"
        };

        ______TS("Typical case: results downloadable");

        InstructorFeedbackQuestionResultsDownloadAction action = getAction(paramsQuestion2);
        FileDownloadResult result = getFileDownloadResult(action);
        
        String expectedDestination = "filedownload?" + "error=false" + "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        String expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName() + "_question2";
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1(result.getFileContent(), session);

        ______TS("Typical case: results within section downloadable");

        action = getAction(paramsQuestion1WithinSection);
        result = getFileDownloadResult(action);

        expectedDestination = "filedownload?" + "error=false" + "&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);

        expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName() + "_Section 1" + "_question1";
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1WithinSection1(result.getFileContent(), session);

        ______TS("Failure case: params with null course id");

        try {
            action = getAction(paramsWithNullCourseId);
            result = getFileDownloadResult(action);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.COURSE_ID),
                         e.getMessage());
        }

        ______TS("Failure case: params with null feedback session name");

        try {
            action = getAction(paramsWithNullFeedbackSessionName);
            result = getFileDownloadResult(action);
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }
    }
    
    private void verifyFileContentForSession1InCourse1(String fileContent,
                                                       FeedbackSessionAttributes session) {
        /*
        full testing of file content is
        in FeedbackSessionsLogicTest.testGetFeedbackSessionResultsSummaryAsCsv()
        */

        String[] expected = {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 2,\"Rate 1 other student's product\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 1 to student 2.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Response from student 2 to student 1.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 3 \"\"to\"\" student 2. Multiline test.\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };
        
        assertTrue(fileContent.startsWith(StringUtils.join(expected, Const.EOL)));
        
    }

    private void verifyFileContentForSession1InCourse1WithinSection1(String fileContent,
                                                                     FeedbackSessionAttributes session) {
        /*
        full testing of file content is
        in FeedbackSessionsLogicTest.testGetFeedbackSessionResultsSummaryAsCsv()
        */
        
        String[] expected = {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "Section Name,\"Section 1\"",
                "",
                "",
                "Question 1,\"What is the best selling point of your product?\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };
        
        assertTrue(fileContent.equals(StringUtils.join(expected, Const.EOL)));

    }

    @Override
    protected InstructorFeedbackQuestionResultsDownloadAction getAction(String... params) {
        return (InstructorFeedbackQuestionResultsDownloadAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

}
