package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.test.driver.CsvChecker;
import teammates.ui.webapi.action.CsvResult;
import teammates.ui.webapi.action.GetSessionResultsAsCsvAction;

/**
 * SUT: {@link GetSessionResultsAsCsvAction}.
 */
public class GetSessionResultsAsCsvActionTest extends BaseActionTest<GetSessionResultsAsCsvAction> {

    private DataBundle dataBundle;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT_CSV;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    private void loadCustomBundle() {
        dataBundle = loadDataBundle("/FeedbackSessionResultsCsvActionTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructorAttributes = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructorAttributes.getGoogleId());
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] paramsNormal = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };
        String[] paramsNormalEitherSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "EITHER",
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };
        String[] paramsNormalFromGiverSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "GIVER",
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };
        String[] paramsNormalToRecipientSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "EVALUEE",
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };
        String[] paramsNormalBothGiverAndRecipientInSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "BOTH",
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };
        String[] paramsWithInvalidSectionDetail = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "ALL",
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };
        String[] paramsWithNullCourseId = {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
        };
        String[] paramsWithNullFeedbackSessionName = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
        };
        String[] paramsWithMissingResponsesShown = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "true",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };

        dataBundle = loadDataBundle("/FeedbackSessionResultsCsvActionTest.json");
        FeedbackSessionAttributes sessionWithStatistics = dataBundle.feedbackSessions.get("sessionWithMcq");
        String[] paramsWithStatisticsShown = {
                Const.ParamsNames.COURSE_ID, sessionWithStatistics.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionWithStatistics.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "true",
        };
        String[] paramsWithStatisticsHidden = {
                Const.ParamsNames.COURSE_ID, sessionWithStatistics.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, sessionWithStatistics.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };

        ______TS("Typical case: results downloadable");

        GetSessionResultsAsCsvAction action = getAction(paramsNormal);
        CsvResult result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1_actionTest.csv");

        ______TS("Typical case: results downloadable showing section from giver or recipient");

        action = getAction(paramsNormalEitherSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Either_actionTest.csv");

        ______TS("Typical case: results downloadable showing section from giver");

        action = getAction(paramsNormalFromGiverSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Giver_actionTest.csv");

        ______TS("Typical case: results downloadable showing section to recipient");

        action = getAction(paramsNormalToRecipientSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Recipient_actionTest.csv");

        ______TS("Typical case: results downloadable showing section from both giver and recipient");

        action = getAction(paramsNormalBothGiverAndRecipientInSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Both_actionTest.csv");

        ______TS("Typical successful case: student last name displayed properly after being specified with braces");

        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student1InCourse1.name = "new name {new last name}";
        logic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(student1InCourse1.course, student1InCourse1.email)
                        .withName(student1InCourse1.name)
                        .build()
        );

        action = getAction(paramsNormal);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1NewLastName_actionTest.csv");

        removeAndRestoreTypicalDataBundle();

        ______TS("Failure case: params with null course id");

        action = getAction(paramsWithNullCourseId);
        GetSessionResultsAsCsvAction finalAction2 = action;

        assertThrows(NullHttpParameterException.class, () -> getJsonResult(finalAction2));

        ______TS("Failure case: params with null feedback session name");

        action = getAction(paramsWithNullFeedbackSessionName);
        GetSessionResultsAsCsvAction finalAction3 = action;

        assertThrows(NullHttpParameterException.class, () -> getJsonResult(finalAction3));

        ______TS("Failure case: params with invalid feedback section detail");
        this.verifyHttpParameterFailure(paramsWithInvalidSectionDetail);

        ______TS("Typical case: results with missing responses shown");
        action = getAction(paramsWithMissingResponsesShown);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsMissingResponsesShown_actionTest.csv");

        loadCustomBundle();

        ______TS("Typical case: results with statistics hidden");

        action = getAction(paramsWithStatisticsHidden);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsStatisticsHidden_actionTest.csv");

        loadCustomBundle();

        ______TS("Typical case: results with statistics shown");

        action = getAction(paramsWithStatisticsShown);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsStatisticsShown_actionTest.csv");

        removeAndRestoreTypicalDataBundle();

        ______TS("Typical case: results downloadable by question");

        int questionNum1 = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1").getQuestionNumber();
        String question1Id = logic.getFeedbackQuestion(accessibleFeedbackSession.getFeedbackSessionName(),
                accessibleFeedbackSession.getCourseId(), questionNum1).getId();
        String[] paramsQuestion1 = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question1Id,
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };

        action = getAction(paramsQuestion1);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1Q1_actionTest.csv");

        ______TS("Typical case: results downloadable by question showing section from giver or recipient");

        int questionNum2 = typicalBundle.feedbackQuestions.get("qn2InSession1InCourse1").getQuestionNumber();
        String question2Id = logic.getFeedbackQuestion(accessibleFeedbackSession.getFeedbackSessionName(),
                accessibleFeedbackSession.getCourseId(), questionNum2).getId();

        String[] paramsQuestion2InSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "EITHER",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question2Id,
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };

        action = getAction(paramsQuestion2InSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Q2Either_actionTest.csv");

        ______TS("Typical case: results downloadable by question showing section from giver");

        String[] paramsQuestion2FromSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "GIVER",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question2Id,
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };

        action = getAction(paramsQuestion2FromSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Q2Giver_actionTest.csv");

        ______TS("Typical case: results downloadable by question showing section to recipient");

        String[] paramsQuestion2ToSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "EVALUEE",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question2Id,
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };

        action = getAction(paramsQuestion2ToSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Q2Recipient_actionTest.csv");

        ______TS("Typical case: results downloadable by question showing both giver and recipient");

        String[] paramsQuestion2BothGiverAndRecipientSection = {
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.SECTION_NAME, "Section 1",
                Const.ParamsNames.SECTION_NAME_DETAIL, "BOTH",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question2Id,
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "false",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "false",
        };

        action = getAction(paramsQuestion2BothGiverAndRecipientSection);
        result = getCsvResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        CsvChecker.verifyCsvContent(result.getContent(), "/feedbackSessionResultsC1S1S1Q2Both_actionTest.csv");

        ______TS("typical: instructor downloads results of his/her course");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "true",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "true",
        };

        GetSessionResultsAsCsvAction a = getAction(submissionParams);
        CsvResult r = getCsvResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        String output = r.getContent();
        String expectedResults = logic.getFeedbackSessionResultSummaryAsCsv(
                accessibleFeedbackSession.getCourseId(),
                accessibleFeedbackSession.getFeedbackSessionName(),
                instructorAttributes.getEmail(),
                true,
                true,
                null
        );

        assertTrue(expectedResults.equals(output));

        ______TS("fail: instructor downloads results of non-existent feedback session");

        String nonexistentFeedbackSession = "nonexistentFeedbackSession";
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, nonexistentFeedbackSession,
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES, "true",
                Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS, "true",
        };

        a = getAction(submissionParams);
        GetSessionResultsAsCsvAction finalA = a;

        assertThrows(EntityNotFoundException.class, () -> getJsonResult(finalA));
    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams;

        ______TS("accessible for authenticated instructor");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
        };
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }

}
