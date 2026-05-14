package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SessionResultsBundle}.
 */
public class SessionResultsBundleTest extends BaseTestCase {

    @Test
    public void testGetQuestionResponseMap() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");

        List<String> allExpectedResponses = new ArrayList<>();
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response1ForQ1").toString());
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response2ForQ1").toString());

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
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
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");

        List<FeedbackMissingResponse> expectedMissingResponses = new ArrayList<>();
        expectedMissingResponses.add(createFeedbackMissingResponse(responseBundle.feedbackResponses.get("response1ForQ1")));
        expectedMissingResponses.add(createFeedbackMissingResponse(responseBundle.feedbackResponses.get("response2ForQ1")));

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(),
                        new ArrayList<>(expectedMissingResponses),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values()))
                );

        ______TS("Test question having missing responses");
        FeedbackQuestion fq = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackMissingResponse> missingResponses = bundle.getQuestionMissingResponseMap().get(fq);
        assertEquals(2, missingResponses.size());
        assertEquals(expectedMissingResponses.get(0), missingResponses.get(0));
        assertEquals(expectedMissingResponses.get(1), missingResponses.get(1));

        ______TS("Test question having no missing responses");
        fq = responseBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        missingResponses = bundle.getQuestionMissingResponseMap().get(fq);
        assertEquals(0, missingResponses.size());
    }

    @Test
    public void testIsResponseGiverRecipientVisible_typicalCase_shouldReturnCorrectValues() {

        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");

        FeedbackSession session1Course1 = getTypicalFeedbackSessionForCourse(getTypicalCourse());

        FeedbackQuestion question1ForS1C1 = getTypicalFeedbackQuestionForSession(session1Course1);
        FeedbackQuestion question2ForS1C1 = getTypicalFeedbackQuestionForSession(session1Course1);

        FeedbackResponse response1ForQ1S1C1 = getTypicalFeedbackResponseForQuestion(question1ForS1C1);
        FeedbackResponse response2ForQ1S1C1 = getTypicalFeedbackResponseForQuestion(question1ForS1C1);
        FeedbackResponse response1ForQ2S1C1 = getTypicalFeedbackResponseForQuestion(question2ForS1C1);
        FeedbackResponse response2ForQ2S1C1 = getTypicalFeedbackResponseForQuestion(question2ForS1C1);

        Map<UUID, FeedbackResponse> responses = new HashMap<>();
        responses.put(response1ForQ1S1C1.getId(), response1ForQ1S1C1);
        responses.put(response2ForQ1S1C1.getId(), response2ForQ1S1C1);
        responses.put(response1ForQ2S1C1.getId(), response1ForQ2S1C1);
        responses.put(response2ForQ2S1C1.getId(), response2ForQ2S1C1);

        Map<UUID, Boolean> responseGiverVisibilityTable = new HashMap<>();
        responseGiverVisibilityTable.put(response1ForQ1S1C1.getId(), true);
        responseGiverVisibilityTable.put(response2ForQ1S1C1.getId(), false);
        responseGiverVisibilityTable.put(response1ForQ2S1C1.getId(), true);
        responseGiverVisibilityTable.put(response2ForQ2S1C1.getId(), false);

        Map<UUID, Boolean> responseRecipientVisibilityTable = new HashMap<>();
        responseRecipientVisibilityTable.put(response1ForQ1S1C1.getId(), false);
        responseRecipientVisibilityTable.put(response2ForQ1S1C1.getId(), true);
        responseRecipientVisibilityTable.put(response1ForQ2S1C1.getId(), true);
        responseRecipientVisibilityTable.put(response2ForQ2S1C1.getId(), false);

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(),
                        responseGiverVisibilityTable,
                        responseRecipientVisibilityTable,
                        new HashMap<>(),
                        new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values()))
                );

        for (Map.Entry<UUID, Boolean> visibilityEntry : responseGiverVisibilityTable.entrySet()) {
            UUID responseId = visibilityEntry.getKey();
            assertEquals(visibilityEntry.getValue(),
                    bundle.isResponseGiverVisible(responseId));
        }

        for (Map.Entry<UUID, Boolean> visibilityEntry : responseRecipientVisibilityTable.entrySet()) {
            UUID responseId = visibilityEntry.getKey();
            QuestionRecipientType recipientType = responses.get(responseId).getFeedbackQuestion().getRecipientType();
            assertEquals(visibilityEntry.getValue(),
                    bundle.isResponseRecipientVisible(responseId, recipientType));
        }
    }

    @Test
    public void testIsCommentGiverVisible_typicalCase_shouldReturnCorrectValues() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");

        UUID commentId1 = UUID.fromString("00000000-0000-4000-8000-000000000001");
        UUID commentId2 = UUID.fromString("00000000-0000-4000-8000-000000000002");
        Map<UUID, Boolean> commentGiverVisibilityTable = new HashMap<>();
        commentGiverVisibilityTable.put(commentId1, true);
        commentGiverVisibilityTable.put(commentId2, false);

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        new ArrayList<>(responseBundle.feedbackQuestions.values()),
                        new HashSet<>(),
                        new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()),
                        new ArrayList<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        commentGiverVisibilityTable,
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values()))
                );

        // Manually add comment IDs as loadDataBundle does not add comment IDs
        FeedbackResponseComment comment1 = responseBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        FeedbackResponseComment comment2 = responseBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1");
        comment1.setId(commentId1);
        comment2.setId(commentId2);

        assertTrue(bundle.isCommentGiverVisible(comment1));
        assertFalse(bundle.isCommentGiverVisible(comment2));
    }

    @Test
    public void testGetAnonGiverName_typicalCase_shouldGenerateCorrectly() {
        String anonName = SessionResultsBundle.getAnonGiverName(QuestionGiverType.STUDENTS, "");
        assertTrue(anonName.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));

        String anonName1 = SessionResultsBundle.getAnonGiverName(QuestionGiverType.STUDENTS, "test@gmail.com");
        String anonName2 = SessionResultsBundle.getAnonGiverName(QuestionGiverType.STUDENTS, "test@gmail.com");
        String anotherAnonName = SessionResultsBundle.getAnonGiverName(
                        QuestionGiverType.STUDENTS, "different@gmail.com");

        assertTrue(anonName1.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));
        assertEquals(anonName1, anonName2);
        assertNotEquals(anonName1, anotherAnonName);
    }

    @Test
    public void testGetAnonRecipientName_typicalCase_shouldGenerateCorrectly() {
        String anonName = SessionResultsBundle.getAnonRecipientName(QuestionRecipientType.STUDENTS, "");
        assertTrue(anonName.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));

        String anonName1 = SessionResultsBundle.getAnonRecipientName(QuestionRecipientType.STUDENTS, "test@gmail.com");
        String anonName2 = SessionResultsBundle.getAnonRecipientName(QuestionRecipientType.STUDENTS, "test@gmail.com");
        String anotherAnonName = SessionResultsBundle.getAnonRecipientName(
                        QuestionRecipientType.STUDENTS, "different@gmail.com");

        assertTrue(anonName1.startsWith(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT));
        assertEquals(anonName1, anonName2);
        assertNotEquals(anonName1, anotherAnonName);
    }

    private FeedbackMissingResponse createFeedbackMissingResponse(FeedbackResponse response) {
        return new FeedbackMissingResponse(
                response.getFeedbackQuestion(),
                response.getGiver(),
                response.getGiverSectionName(),
                response.getRecipient(),
                response.getRecipientSectionName()
        );
    }
}
