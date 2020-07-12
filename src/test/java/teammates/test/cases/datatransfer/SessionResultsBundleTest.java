package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link SessionResultsBundle}.
 */
public class SessionResultsBundleTest extends BaseTestCase {

    @Test
    public void testGetQuestionResponseMap() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("session1InCourse1");

        List<String> allExpectedResponses = new ArrayList<>();
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response1ForQ1S1C1").toString());
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response2ForQ1S1C1").toString());

        SessionResultsBundle bundle =
                new SessionResultsBundle(session,
                        responseBundle.feedbackQuestions, new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
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

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("session1InCourse1");

        List<String> expectedMissingResponses = new ArrayList<>();
        expectedMissingResponses.add(responseBundle.feedbackResponses.get("response1ForQ1S1C1").toString());
        expectedMissingResponses.add(responseBundle.feedbackResponses.get("response2ForQ1S1C1").toString());

        SessionResultsBundle bundle =
                new SessionResultsBundle(session,
                        responseBundle.feedbackQuestions, new ArrayList<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new HashMap<>(), new HashMap<>(), new HashMap<>(),
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

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("session1InCourse1");

        Map<String, boolean[]> responseVisibilityTable = new HashMap<>();
        responseVisibilityTable.put("response1ForQ1S1C1", new boolean[] {true, false});
        responseVisibilityTable.put("response2ForQ1S1C1", new boolean[] {false, true});
        responseVisibilityTable.put("response1ForQ2S1C1", new boolean[] {true, true});
        responseVisibilityTable.put("response2ForQ2S1C1", new boolean[] {false, false});

        SessionResultsBundle bundle =
                new SessionResultsBundle(session,
                        responseBundle.feedbackQuestions, new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(), responseVisibilityTable, new HashMap<>(), new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values())));

        for (Map.Entry<String, boolean[]> visibilityEntry : responseVisibilityTable.entrySet()) {
            assertEquals(visibilityEntry.getValue()[Const.VISIBILITY_TABLE_GIVER],
                    bundle.isResponseGiverVisible(responseBundle.feedbackResponses.get(visibilityEntry.getKey())));
        }
    }

    @Test
    public void testIsCommentGiverVisible_typicalCase_shouldReturnCorrectValues() {

        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("session1InCourse1");

        Map<Long, boolean[]> commentVisibilityTable = new HashMap<>();
        commentVisibilityTable.put(1L, new boolean[] {true});
        commentVisibilityTable.put(2L, new boolean[] {false});

        SessionResultsBundle bundle =
                new SessionResultsBundle(session,
                        responseBundle.feedbackQuestions, new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(), new HashMap<>(), new HashMap<>(), commentVisibilityTable,
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
