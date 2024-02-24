package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SqlSessionResultsBundle}.
 */
public class SqlSessionResultsBundleTest extends BaseTestCase {

    @Test
    public void testGetQuestionResponseMap() {
        SqlDataBundle responseBundle = loadSqlDataBundle("/SqlFeedbackSessionResultsBundleTest.json");

        List<String> allExpectedResponses = new ArrayList<>();
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response1ForQ1").toString());
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response2ForQ1").toString());

        SqlSessionResultsBundle bundle =
                new SqlSessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new SqlCourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values()))
                );

        ______TS("Test question having responses");
        FeedbackQuestion fq = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponse> allResponses = bundle.getQuestionResponseMap().get(fq);
        assertEquals(2, allResponses.size());
        List<String> allResponsesString = new ArrayList<>();
        allResponsesString.add(allResponses.get(0).toString());
        allResponsesString.add(allResponses.get(1).toString());
        assertEquals(allExpectedResponses, allResponsesString);

        ______TS("Test question having no responses");
        fq = responseBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        allResponses = bundle.getQuestionResponseMap().get(fq);
        assertEquals(0, allResponses.size());
    }

    @Test
    public void testGetQuestionMissingResponseMap() {
        SqlDataBundle responseBundle = loadSqlDataBundle("/SqlFeedbackSessionResultsBundleTest.json");

        List<String> expectedMissingResponses = new ArrayList<>();
        expectedMissingResponses.add(responseBundle.feedbackResponses.get("response1ForQ1").toString());
        expectedMissingResponses.add(responseBundle.feedbackResponses.get("response2ForQ1").toString());

        SqlSessionResultsBundle bundle =
                new SqlSessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new SqlCourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values()))
                );

        ______TS("Test question having missing responses");
        FeedbackQuestion fq = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponse> missingResponses = bundle.getQuestionMissingResponseMap().get(fq);
        assertEquals(2, missingResponses.size());
        List<String> missingResponsesString = new ArrayList<>();
        missingResponsesString.add(missingResponses.get(0).toString());
        missingResponsesString.add(missingResponses.get(1).toString());
        assertEquals(expectedMissingResponses, missingResponsesString);

        ______TS("Test question having no missing responses");
        fq = responseBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        missingResponses = bundle.getQuestionMissingResponseMap().get(fq);
        assertEquals(0, missingResponses.size());
    }

    @Test
    public void testIsResponseGiverRecipientVisible_typicalCase_shouldReturnCorrectValues() {

        SqlDataBundle responseBundle = loadSqlDataBundle("/SqlFeedbackSessionResultsBundleTest.json");

        FeedbackSession session1Course1 = getTypicalFeedbackSessionForCourse(getTypicalCourse());

        FeedbackQuestion question1ForS1C1 = getTypicalFeedbackQuestionForSession(session1Course1);
        FeedbackQuestion question2ForS1C1 = getTypicalFeedbackQuestionForSession(session1Course1);

        FeedbackResponse response1ForQ1S1C1 = getTypicalFeedbackResponseForQuestion(question1ForS1C1);
        FeedbackResponse response2ForQ1S1C1 = getTypicalFeedbackResponseForQuestion(question1ForS1C1);
        FeedbackResponse response1ForQ2S1C1 = getTypicalFeedbackResponseForQuestion(question2ForS1C1);
        FeedbackResponse response2ForQ2S1C1 = getTypicalFeedbackResponseForQuestion(question2ForS1C1);

        Map<FeedbackResponse, Boolean> responseGiverVisibilityTable = new HashMap<>();
        responseGiverVisibilityTable.put(response1ForQ1S1C1, true);
        responseGiverVisibilityTable.put(response2ForQ1S1C1, false);
        responseGiverVisibilityTable.put(response1ForQ2S1C1, true);
        responseGiverVisibilityTable.put(response2ForQ2S1C1, false);

        Map<FeedbackResponse, Boolean> responseRecipientVisibilityTable = new HashMap<>();
        responseRecipientVisibilityTable.put(response1ForQ1S1C1, false);
        responseRecipientVisibilityTable.put(response2ForQ1S1C1, true);
        responseRecipientVisibilityTable.put(response1ForQ2S1C1, true);
        responseRecipientVisibilityTable.put(response2ForQ2S1C1, false);

        SqlSessionResultsBundle bundle =
                new SqlSessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(),
                        responseGiverVisibilityTable,
                        responseRecipientVisibilityTable,
                        new HashMap<>(),
                        new HashMap<>(),
                        new SqlCourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values()))
                );

        for (Map.Entry<FeedbackResponse, Boolean> visibilityEntry : responseGiverVisibilityTable.entrySet()) {
            assertEquals(visibilityEntry.getValue(),
                    bundle.isResponseGiverVisible(visibilityEntry.getKey()));
        }

        for (Map.Entry<FeedbackResponse, Boolean> visibilityEntry : responseRecipientVisibilityTable.entrySet()) {
            assertEquals(visibilityEntry.getValue(),
                    bundle.isResponseRecipientVisible(visibilityEntry.getKey()));
        }
    }

    @Test
    public void testIsCommentGiverVisible_typicalCase_shouldReturnCorrectValues() {
        SqlDataBundle responseBundle = loadSqlDataBundle("/SqlFeedbackSessionResultsBundleTest.json");

        Map<Long, Boolean> commentGiverVisibilityTable = new HashMap<>();
        commentGiverVisibilityTable.put(1L, true);
        commentGiverVisibilityTable.put(2L, false);

        SqlSessionResultsBundle bundle =
                new SqlSessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        commentGiverVisibilityTable,
                        new SqlCourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values()))
                );

        // Manually add comment IDs as loadSqlDataBundle does not add comment IDs
        FeedbackResponseComment comment1 = responseBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        FeedbackResponseComment comment2 = responseBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1");
        comment1.setId(1L);
        comment2.setId(2L);

        assertTrue(bundle.isCommentGiverVisible(comment1));
        assertFalse(bundle.isCommentGiverVisible(comment2));
    }

    @Test
    public void testGetAnonName_typicalCase_shouldGenerateCorrectly() {
        String anonName = SqlSessionResultsBundle.getAnonName(FeedbackParticipantType.STUDENTS, "");
        assertTrue(anonName.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));

        anonName = SqlSessionResultsBundle.getAnonName(FeedbackParticipantType.STUDENTS, "test@gmail.com");
        assertTrue(anonName.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));
    }
}
