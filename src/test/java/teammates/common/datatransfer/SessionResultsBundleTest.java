package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SessionResultsBundle}.
 */
public class SessionResultsBundleTest extends BaseTestCase {

    @Test
    public void testGetQuestionResponseMap() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");
        populateQuestionAndResponseIds(responseBundle);

        List<String> allExpectedResponses = new ArrayList<>();
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response1ForQ1S1C1").toString());
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response2ForQ1S1C1").toString());

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        responseBundle.feedbackQuestions, new HashMap<>(), new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()), new ArrayList<>(),
                        new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values())));

        ______TS("Test question having responses");
        FeedbackQuestionAttributes fqa = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponseAttributes> allResponses = bundle.getQuestionResponseMap().get(fqa.getId());
        assertEquals(2, allResponses.size());
        List<String> allResponsesString = new ArrayList<>();
        allResponsesString.add(allResponses.get(0).toString());
        allResponsesString.add(allResponses.get(1).toString());
        assertEquals(allExpectedResponses, allResponsesString);

        ______TS("Test question having no responses");
        fqa = responseBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        allResponses = bundle.getQuestionResponseMap().get(fqa.getId());
        assertEquals(0, allResponses.size());
    }

    @Test
    public void testGetQuestionMissingResponseMap() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");
        populateQuestionAndResponseIds(responseBundle);

        List<String> expectedMissingResponses = new ArrayList<>();
        expectedMissingResponses.add(responseBundle.feedbackResponses.get("response1ForQ1S1C1").toString());
        expectedMissingResponses.add(responseBundle.feedbackResponses.get("response2ForQ1S1C1").toString());

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        responseBundle.feedbackQuestions, new HashMap<>(), new HashSet<>(),
                        new ArrayList<>(), new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values())));

        ______TS("Test question having missing responses");
        FeedbackQuestionAttributes fqa = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponseAttributes> missingResponses = bundle.getQuestionMissingResponseMap().get(fqa.getId());
        assertEquals(2, missingResponses.size());
        List<String> missingResponsesString = new ArrayList<>();
        missingResponsesString.add(missingResponses.get(0).toString());
        missingResponsesString.add(missingResponses.get(1).toString());
        assertEquals(expectedMissingResponses, missingResponsesString);

        ______TS("Test question having no missing responses");
        fqa = responseBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        missingResponses = bundle.getQuestionMissingResponseMap().get(fqa.getId());
        assertEquals(0, missingResponses.size());
    }

    @Test
    public void testIsResponseGiverRecipientVisible_typicalCase_shouldReturnCorrectValues() {

        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");
        populateQuestionAndResponseIds(responseBundle);

        Map<String, Boolean> responseGiverVisibilityTable = new HashMap<>();
        responseGiverVisibilityTable.put("response1ForQ1S1C1", true);
        responseGiverVisibilityTable.put("response2ForQ1S1C1", false);
        responseGiverVisibilityTable.put("response1ForQ2S1C1", true);
        responseGiverVisibilityTable.put("response2ForQ2S1C1", false);

        Map<String, Boolean> responseRecipientVisibilityTable = new HashMap<>();
        responseRecipientVisibilityTable.put("response1ForQ1S1C1", false);
        responseRecipientVisibilityTable.put("response2ForQ1S1C1", true);
        responseRecipientVisibilityTable.put("response1ForQ2S1C1", true);
        responseRecipientVisibilityTable.put("response2ForQ2S1C1", false);

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        responseBundle.feedbackQuestions, new HashMap<>(), new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()), new ArrayList<>(),
                        responseGiverVisibilityTable, responseRecipientVisibilityTable,
                        new HashMap<>(), new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values())));

        for (Map.Entry<String, Boolean> visibilityEntry : responseGiverVisibilityTable.entrySet()) {
            assertEquals(visibilityEntry.getValue(),
                    bundle.isResponseGiverVisible(responseBundle.feedbackResponses.get(visibilityEntry.getKey())));
        }

        for (Map.Entry<String, Boolean> visibilityEntry : responseRecipientVisibilityTable.entrySet()) {
            assertEquals(visibilityEntry.getValue(),
                    bundle.isResponseRecipientVisible(responseBundle.feedbackResponses.get(visibilityEntry.getKey())));
        }
    }

    @Test
    public void testIsCommentGiverVisible_typicalCase_shouldReturnCorrectValues() {

        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");
        populateQuestionAndResponseIds(responseBundle);

        Map<Long, Boolean> commentGiverVisibilityTable = new HashMap<>();
        commentGiverVisibilityTable.put(1L, true);
        commentGiverVisibilityTable.put(2L, false);

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        responseBundle.feedbackQuestions, new HashMap<>(), new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()), new ArrayList<>(),
                        new HashMap<>(), new HashMap<>(), new HashMap<>(), commentGiverVisibilityTable,
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values())));

        assertTrue(bundle.isCommentGiverVisible(responseBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1")));
        assertFalse(bundle.isCommentGiverVisible(responseBundle.feedbackResponseComments.get("comment2FromT1C1ToR1Q1S1C1")));
    }

    @Test
    public void testGetAnonName_typicalCase_shouldGenerateCorrectly() {
        String anonName = SessionResultsBundle.getAnonName(FeedbackParticipantType.STUDENTS, "");
        assertTrue(anonName.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));

        anonName = SessionResultsBundle.getAnonName(FeedbackParticipantType.STUDENTS, "test@gmail.com");
        assertTrue(anonName.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));
    }
}
