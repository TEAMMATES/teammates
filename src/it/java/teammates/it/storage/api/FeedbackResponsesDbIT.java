package teammates.it.storage.api;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;

/**
 * SUT: {@link FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbIT extends BaseTestCaseWithDatabaseAccess {

    private final FeedbackResponsesDb frDb = FeedbackResponsesDb.inst();

    private DataBundle testDataBundle;

    @BeforeClass
    public void setupClass() {
        testDataBundle = loadDataBundle("/FeedbackResponsesITBundle.json");
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(testDataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion fq = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        Student giver = testDataBundle.students.get("student1InCourse1");

        List<FeedbackResponse> expectedQuestions = List.of(
                testDataBundle.feedbackResponses.get("response1ForQ1"),
                testDataBundle.feedbackResponses.get("response3ForQ1")
        );

        List<FeedbackResponse> actualQuestions =
                frDb.getFeedbackResponsesFromGiverForQuestion(fq.getId(), giver.getId(), null);

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testDeleteFeedback() {
        ______TS("success: typical case");
        FeedbackResponse fr1 = testDataBundle.feedbackResponses.get("response1ForQ1");

        frDb.deleteFeedbackResponse(fr1);

        assertNull(frDb.getFeedbackResponse(fr1.getId()));
    }

    @Test
    public void testAreThereResponsesForQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion fq1 = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        boolean actualResponse1 =
                frDb.areThereResponsesForQuestion(fq1.getId());

        assertTrue(actualResponse1);

        ______TS("feedback question with no responses");
        FeedbackQuestion fq2 = testDataBundle.feedbackQuestions.get("qn6InSession1InCourse1NoResponses");

        boolean actualResponse2 =
                frDb.areThereResponsesForQuestion(fq2.getId());

        assertFalse(actualResponse2);
    }

    @Test
    public void testHasResponsesForCourse() {
        ______TS("success: typical case");
        Course course = testDataBundle.courses.get("course1");

        boolean actual =
                frDb.hasResponsesForCourse(course.getId());

        assertTrue(actual);
    }

    @Test
    public void testGetFeedbackResponsesForRecipientForQuestion_matchNotFound_shouldReturnEmptyList() {
        ______TS("Question not found");
        Student recipient = testDataBundle.students.get("student1InCourse1");
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        List<FeedbackResponse> results = frDb.getFeedbackResponsesForRecipientForQuestion(
                nonexistentQuestionId, recipient.getId(), null);
        assertEquals(0, results.size());

        ______TS("No matching responses exist");
        FeedbackQuestion questionWithNoResponses = testDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        results = frDb.getFeedbackResponsesForRecipientForQuestion(questionWithNoResponses.getId(), recipient.getId(), null);
        assertEquals(0, results.size());

    }

    @Test
    public void testGetFeedbackResponsesForRecipientForQuestion_matchFound_success() {
        ______TS("Matching responses exist");
        Student recipient = testDataBundle.students.get("student2InCourse1");
        FeedbackQuestion question = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponse> expected = List.of(
                testDataBundle.feedbackResponses.get("response2ForQ1")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForRecipientForQuestion(
                question.getId(), recipient.getId(), null);
        assertListResponsesEqual(expected, actual);

    }

    @Test
    public void testGetFeedbackResponsesForSession() {
        ______TS("Session has responses");
        FeedbackSession sessionWithResponses = testDataBundle.feedbackSessions.get("session1InCourse1");
        List<FeedbackResponse> expected = List.of(
                testDataBundle.feedbackResponses.get("response1ForQ1"),
                testDataBundle.feedbackResponses.get("response2ForQ1"),
                testDataBundle.feedbackResponses.get("response1ForQ2"),
                testDataBundle.feedbackResponses.get("response2ForQ2"),
                testDataBundle.feedbackResponses.get("response1ForQ3"),
                testDataBundle.feedbackResponses.get("response3ForQ1"),
                testDataBundle.feedbackResponses.get("response3ForQ2"),
                testDataBundle.feedbackResponses.get("response4ForQ1")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForSession(sessionWithResponses,
                sessionWithResponses.getCourseId());
        assertListResponsesEqual(expected, actual);

        ______TS("Session has no responses");
        FeedbackSession sessionWithoutResponses = testDataBundle.feedbackSessions.get(
                "unpublishedSession1InTypicalCourse");
        actual = frDb.getFeedbackResponsesForSession(sessionWithoutResponses, sessionWithResponses.getCourseId());
        assertEquals(0, actual.size());
    }

    private void assertListResponsesEqual(List<FeedbackResponse> expected, List<FeedbackResponse> actual) {
        assertEquals("List size not equal.", expected.size(), actual.size());
        assertTrue(
                String.format("List contents are not equal.%nExpected: %s,%nActual: %s",
                        expected.toString(), actual.toString()),
                new HashSet<>(expected).equals(new HashSet<>(actual)));
    }
}
