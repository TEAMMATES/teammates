package teammates.test.cases.action;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.controller.FileDownloadResult;
import teammates.ui.controller.InstructorFeedbackResultsDownloadAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackResultsDownloadAction}.
 */
public class InstructorFeedbackResultsDownloadActionTest extends BaseActionTest {

    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        gaeSimulation.loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsNormal = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName()
        };
        String[] paramsNormalWithinSection = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1"
        };

        String[] paramsWithLargeData = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                "simulateExcessDataForTesting", "true"
        };

        String[] paramsWithNullCourseId = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName()
        };
        String[] paramsWithNullFeedbackSessionName = {
                Const.ParamsNames.COURSE_ID, session.getCourseId()
        };

        String[] paramsWithMissingResponsesShown = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "true"
        };

        String[] paramsWithMissingResponsesHidden = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false"
        };

        ______TS("Typical case: results downloadable");

        InstructorFeedbackResultsDownloadAction action = getAction(paramsNormal);
        FileDownloadResult result = getFileDownloadResult(action);

        String expectedDestination = getPageResultDestination("filedownload", false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        String expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName();
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1(result.getFileContent(), session);

        ______TS("Typical successful case: student last name displayed properly after being specified with braces");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student1InCourse1.name = "new name {new last name}";
        StudentsLogic studentsLogic = StudentsLogic.inst();
        studentsLogic.updateStudentCascade(student1InCourse1.email, student1InCourse1);

        action = getAction(paramsNormal);
        result = getFileDownloadResult(action);

        expectedDestination = getPageResultDestination("filedownload", false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName();
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1WithNewLastName(result.getFileContent(), session);

        removeAndRestoreTypicalDataBundle();

        ______TS("Typical case: results within section downloadable");

        action = getAction(paramsNormalWithinSection);
        result = getFileDownloadResult(action);

        expectedDestination = getPageResultDestination("filedownload", false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);

        expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName() + "_Section 1";
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForSession1InCourse1WithinSection1(result.getFileContent(), session);

        ______TS("Mock case to throw ExceedingRangeException: data is too large to be downloaded in one go");

        action = getAction(paramsWithLargeData);
        RedirectResult r = getRedirectResult(action);

        expectedDestination = getPageResultDestination(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE, true, "");
        expectedDestination = Config.getAppUrl(expectedDestination)
                .withCourseId(session.getCourseId()).withUserId("idOfInstructor1OfCourse1")
                .withSessionName(session.getFeedbackSessionName()).toAbsoluteString();
        assertEquals(new URL(expectedDestination).getFile(), r.getDestinationWithParams());
        assertTrue(r.isError);

        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_DOWNLOAD_FILE_SIZE_EXCEEDED, r.getStatusMessage());

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

        ______TS("Typical case: results with missing responses shown");
        action = getAction(paramsWithMissingResponsesShown);
        result = getFileDownloadResult(action);
        expectedDestination = getPageResultDestination("filedownload", false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);

        expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName();
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForDownloadWithMissingResponsesShown(result.getFileContent(), session);

        ______TS("Typical case: results with missing responses hidden");
        action = getAction(paramsWithMissingResponsesHidden);
        result = getFileDownloadResult(action);
        expectedDestination = getPageResultDestination("filedownload", false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);

        expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName();
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForDownloadWithMissingResponsesHidden(result.getFileContent(), session);

        ______TS("Typical case: results downloadable by question");

        final int questionNum2 = typicalBundle.feedbackQuestions.get("qn2InSession1InCourse1").getQuestionNumber();
        final String question2Id = fqLogic.getFeedbackQuestion(session.getFeedbackSessionName(),
                session.getCourseId(), questionNum2).getId();
        String[] paramsQuestion2 = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question2Id
        };

        action = getAction(paramsQuestion2);
        result = getFileDownloadResult(action);

        expectedDestination = getPageResultDestination("filedownload", false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());

        expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName() + "_question2";
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForQuestion2Session1InCourse1(result.getFileContent(), session);

        ______TS("Typical case: results within section downloadable by question");

        final int questionNum1 = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1").getQuestionNumber();
        final String question1Id = fqLogic.getFeedbackQuestion(session.getFeedbackSessionName(),
                session.getCourseId(), questionNum1).getId();

        String[] paramsQuestion1WithinSection = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question1Id
        };

        action = getAction(paramsQuestion1WithinSection);
        result = getFileDownloadResult(action);

        expectedDestination = getPageResultDestination("filedownload", false, "idOfInstructor1OfCourse1");
        assertEquals(expectedDestination, result.getDestinationWithParams());
        assertFalse(result.isError);

        expectedFileName = session.getCourseId() + "_" + session.getFeedbackSessionName() + "_Section 1" + "_question1";
        assertEquals(expectedFileName, result.getFileName());
        verifyFileContentForQuestion1Session1InCourse1WithinSection1(result.getFileContent(), session);
    }

    private void verifyFileContentForDownloadWithMissingResponsesShown(String fileContent,
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
                "Question 1,\"What is the best selling point of your product?\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback,Comment From,Comment",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\",Instructor1 Course1,\"Instructor 1 comment to student 1 self feedback\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\""
                // CHECKSTYLE.ON:LineLength
        };

        assertTrue(fileContent.startsWith(StringUtils.join(expected, System.lineSeparator())));

    }

    private void verifyFileContentForDownloadWithMissingResponsesHidden(String fileContent,
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
                "Question 1,\"What is the best selling point of your product?\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback,Comment From,Comment",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\",Instructor1 Course1,\"Instructor 1 comment to student 1 self feedback\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\""
                // CHECKSTYLE.ON:LineLength
        };
        assertTrue(fileContent.startsWith(StringUtils.join(expected, System.lineSeparator())));

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
                "Question 1,\"What is the best selling point of your product?\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback,Comment From,Comment",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\",Instructor1 Course1,\"Instructor 1 comment to student 1 self feedback\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\""
                // CHECKSTYLE.ON:LineLength
        };

        assertTrue(fileContent.startsWith(StringUtils.join(expected, System.lineSeparator())));

    }

    private void verifyFileContentForSession1InCourse1WithNewLastName(String fileContent,
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
                "Question 1,\"What is the best selling point of your product?\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback,Comment From,Comment",
                "\"Team 1.1</td></div>'\"\"\",\"new name new last name\",\"new last name\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"new name new last name\",\"new last name\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\",Instructor1 Course1,\"Instructor 1 comment to student 1 self feedback\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\""
                // CHECKSTYLE.ON:LineLength
        };

        assertTrue(fileContent.startsWith(StringUtils.join(expected, System.lineSeparator())));

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
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\""
                // CHECKSTYLE.ON:LineLength
        };

        assertTrue(fileContent.startsWith(StringUtils.join(expected, System.lineSeparator())));

    }

    private void verifyFileContentForQuestion2Session1InCourse1(String fileContent,
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
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback,Comment From,Comment",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 1 to student 2.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Response from student 2 to student 1.\",Instructor1 Course1,\"Instructor 1 comment to student 1 self feedback Question 2\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 3 \"\"to\"\" student 2. Multiline test.\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };
        assertTrue(fileContent.startsWith(StringUtils.join(expected, System.lineSeparator())));
    }

    private void verifyFileContentForQuestion1Session1InCourse1WithinSection1(String fileContent,
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

        assertEquals(StringUtils.join(expected, System.lineSeparator()), fileContent);

    }

    @Override
    protected InstructorFeedbackResultsDownloadAction getAction(String... params) {
        return (InstructorFeedbackResultsDownloadAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName()
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
