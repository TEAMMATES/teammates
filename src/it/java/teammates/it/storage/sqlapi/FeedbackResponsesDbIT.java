package teammates.it.storage.sqlapi;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.responses.FeedbackTextResponse;

/**
 * SUT: {@link FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackResponsesDb frDb = FeedbackResponsesDb.inst();
    private final FeedbackResponseCommentsDb frcDb = FeedbackResponseCommentsDb.inst();

    private SqlDataBundle testDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        testDataBundle = loadSqlDataBundle("/FeedbackResponsesITBundle.json");
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

        List<FeedbackResponse> expectedQuestions = List.of(
                testDataBundle.feedbackResponses.get("response1ForQ1"),
                testDataBundle.feedbackResponses.get("response3ForQ1")
        );

        List<FeedbackResponse> actualQuestions =
                frDb.getFeedbackResponsesFromGiverForQuestion(fq.getId(), "student1@teammates.tmt");

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade() {
        ______TS("success: typical case");
        FeedbackQuestion fq = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponse fr1 = testDataBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponse fr2 = testDataBundle.feedbackResponses.get("response2ForQ1");
        FeedbackResponseComment frc1 = testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");

        frDb.deleteFeedbackResponsesForQuestionCascade(fq.getId());

        assertNull(frDb.getFeedbackResponse(fr1.getId()));
        assertNull(frDb.getFeedbackResponse(fr2.getId()));
        assertNull(frcDb.getFeedbackResponseComment(frc1.getId()));
    }

    @Test
    public void testDeleteFeedback() {
        ______TS("success: typical case");
        FeedbackResponse fr1 = testDataBundle.feedbackResponses.get("response1ForQ1");

        frDb.deleteFeedbackResponse(fr1);

        assertNull(frDb.getFeedbackResponse(fr1.getId()));
    }

    @Test
    public void testHasResponsesFromGiverInSession() {
        ______TS("success: typical case");
        Course course = testDataBundle.courses.get("course1");
        FeedbackSession fs = testDataBundle.feedbackSessions.get("session1InCourse1");

        boolean actualHasReponses1 =
                frDb.hasResponsesFromGiverInSession("student1@teammates.tmt", fs.getName(), course.getId());

        assertTrue(actualHasReponses1);

        ______TS("student with no responses");
        boolean actualHasReponses2 =
                frDb.hasResponsesFromGiverInSession("studentnorespones@teammates.tmt", fs.getName(), course.getId());

        assertFalse(actualHasReponses2);
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

    private FeedbackResponse prepareSqlInjectionTest() {
        FeedbackResponse fr = testDataBundle.feedbackResponses.get("response1ForQ1");
        assertNotNull(frDb.getFeedbackResponse(fr.getId()));

        return fr;
    }

    private void checkSqliFailed(FeedbackResponse fr) {
        // If SQLi is successful, feedback responses would have been deleted from db.
        // So get will return null.
        assertNotNull(frDb.getFeedbackResponse(fr.getId()));
    }

    @Test
    public void testSqlInjectionInGetFeedbackResponsesFromGiverForCourse() {
        FeedbackResponse fr = prepareSqlInjectionTest();

        ______TS("SQL Injection test in GetFeedbackResponsesFromGiverForCourse, courseId param");
        String courseId = "'; DELETE FROM feedback_responses;--";
        frDb.getFeedbackResponsesFromGiverForCourse(courseId, "");

        checkSqliFailed(fr);
    }

    @Test
    public void testSqlInjectionInGetFeedbackResponsesForRecipientForCourse() {
        FeedbackResponse fr = prepareSqlInjectionTest();

        ______TS("SQL Injection test in GetFeedbackResponsesForRecipientForCourse, courseId param");
        String courseId = "'; DELETE FROM feedback_responses;--";
        frDb.getFeedbackResponsesForRecipientForCourse(courseId, "");

        checkSqliFailed(fr);
    }

    @Test
    public void testSqlInjectionInGetFeedbackResponsesFromGiverForQuestion() {
        FeedbackResponse fr = prepareSqlInjectionTest();

        ______TS("SQL Injection test in GetFeedbackResponsesFromGiverForQuestion, giverEmail param");
        String giverEmail = "';/**/DELETE/**/FROM/**/feedback_responses;--@gmail.com";
        frDb.getFeedbackResponsesFromGiverForQuestion(fr.getId(), giverEmail);

        checkSqliFailed(fr);
    }

    @Test
    public void testSqlInjectionInHasResponsesFromGiverInSession() {
        FeedbackResponse fr = prepareSqlInjectionTest();

        ______TS("SQL Injection test in HasResponsesFromGiverInSession, giver param");
        String giver = "'; DELETE FROM feedback_responses;--";
        frDb.hasResponsesFromGiverInSession(giver, "", "");

        checkSqliFailed(fr);
    }

    @Test
    public void testSqlInjectionInHasResponsesForCourse() {
        FeedbackResponse fr = prepareSqlInjectionTest();

        ______TS("SQL Injection test in HasResponsesForCourse, courseId param");
        String courseId = "'; DELETE FROM feedback_responses;--";
        frDb.hasResponsesForCourse(courseId);

        checkSqliFailed(fr);
    }

    @Test
    public void testSqlInjectionInCreateFeedbackResponse() throws Exception {
        FeedbackResponse fr = prepareSqlInjectionTest();

        FeedbackQuestion fq = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        Section s = testDataBundle.sections.get("section1InCourse1");
        String dummyUuid = "00000000-0000-4000-8000-000000000001";
        FeedbackResponseDetails frd = new FeedbackTextResponseDetails();

        String sqli = "', " + dummyUuid + ", " + dummyUuid + "); DELETE FROM feedback_responses;--";

        FeedbackResponse newFr = new FeedbackTextResponse(fq, "", s, sqli, s, frd);
        frDb.createFeedbackResponse(newFr);

        checkSqliFailed(fr);
    }

    @Test
    public void testSqlInjectionInCpdateFeedbackResponse() throws Exception {
        FeedbackResponse fr = prepareSqlInjectionTest();

        String sqli = "''); DELETE FROM feedback_response_comments;--";
        fr.setGiver(sqli);
        frDb.updateFeedbackResponse(fr);

        checkSqliFailed(fr);
    }

    @Test
    public void testGetFeedbackResponsesForRecipientForQuestion_matchNotFound_shouldReturnEmptyList() {
        ______TS("Question not found");
        String recipient = "student1@teammates.tmt";
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        List<FeedbackResponse> results = frDb.getFeedbackResponsesForRecipientForQuestion(nonexistentQuestionId, recipient);
        assertEquals(0, results.size());

        ______TS("No matching responses exist");
        FeedbackQuestion questionWithNoResponses = testDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        results = frDb.getFeedbackResponsesForRecipientForQuestion(questionWithNoResponses.getId(), recipient);
        assertEquals(0, results.size());

    }

    @Test
    public void testGetFeedbackResponsesForRecipientForQuestion_matchFound_success() {
        ______TS("Matching responses exist");
        String recipient = "student2@teammates.tmt";
        FeedbackQuestion question = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponse> expected = List.of(
                testDataBundle.feedbackResponses.get("response2ForQ1")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForRecipientForQuestion(question.getId(), recipient);
        assertListResponsesEqual(expected, actual);

    }

    @Test
    public void testGetFeedbackResponsesForSessionInSection_matchNotFound_shouldReturnEmptyList() {
        String section3 = testDataBundle.sections.get("section3InCourse1").getName();
        FeedbackSession session = testDataBundle.feedbackSessions.get("session1InCourse1");
        String courseId = session.getCourse().getId();

        ______TS("No matching responses exist for giver section");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.GIVER;
        List<FeedbackResponse> results = frDb.getFeedbackResponsesForSessionInSection(
                session, courseId, section3, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for recipient section");
        fetchType = FeedbackResultFetchType.RECEIVER;
        results = frDb.getFeedbackResponsesForSessionInSection(session, courseId, section3, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for both giver and recipient section");
        fetchType = FeedbackResultFetchType.BOTH;
        results = frDb.getFeedbackResponsesForSessionInSection(session, courseId, section3, fetchType);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponsesForSessionInSection_matchFound_success() {
        Course course = testDataBundle.courses.get("course1");
        FeedbackSession session1 = testDataBundle.feedbackSessions.get("session1InCourse1");
        Section section1 = testDataBundle.sections.get("section1InCourse1");
        Section section2 = testDataBundle.sections.get("section2InCourse1");

        ______TS("Match giver section 1 in session 1");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.GIVER;
        List<FeedbackResponse> expected = List.of(
                testDataBundle.feedbackResponses.get("response1ForQ1"),
                testDataBundle.feedbackResponses.get("response2ForQ1"),
                testDataBundle.feedbackResponses.get("response1ForQ2"),
                testDataBundle.feedbackResponses.get("response2ForQ2"),
                testDataBundle.feedbackResponses.get("response1ForQ3"),
                testDataBundle.feedbackResponses.get("response3ForQ1"),
                testDataBundle.feedbackResponses.get("response3ForQ2")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForSessionInSection(
                session1, course.getId(), section1.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match recipient section 2 in session 1");
        fetchType = FeedbackResultFetchType.RECEIVER;
        expected = List.of(
                testDataBundle.feedbackResponses.get("response3ForQ1"),
                testDataBundle.feedbackResponses.get("response3ForQ2"),
                testDataBundle.feedbackResponses.get("response4ForQ1")
        );
        actual = frDb.getFeedbackResponsesForSessionInSection(session1, course.getId(),
                section2.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match both giver and recipient section 2 in session 1");
        fetchType = FeedbackResultFetchType.BOTH;
        expected = List.of(
                testDataBundle.feedbackResponses.get("response4ForQ1")
        );
        actual = frDb.getFeedbackResponsesForSessionInSection(session1, course.getId(),
                section2.getName(), fetchType);
        assertListResponsesEqual(expected, actual);
    }

    @Test
    public void testGetFeedbackResponsesForQuestionInSection_matchNotFound_shouldReturnEmptyList() {
        String section1 = testDataBundle.sections.get("section1InCourse1").getName();
        String section3 = testDataBundle.sections.get("section3InCourse1").getName();

        ______TS("Question not found");
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.BOTH;
        List<FeedbackResponse> results = frDb.getFeedbackResponsesForQuestionInSection(nonexistentQuestionId,
                section1, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for giver section");
        UUID questionId = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1").getId();
        fetchType = FeedbackResultFetchType.GIVER;
        results = frDb.getFeedbackResponsesForQuestionInSection(questionId, section3, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for recipient section");
        fetchType = FeedbackResultFetchType.RECEIVER;
        results = frDb.getFeedbackResponsesForQuestionInSection(questionId, section3, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for both giver and recipient section");
        fetchType = FeedbackResultFetchType.BOTH;
        results = frDb.getFeedbackResponsesForQuestionInSection(questionId, section3, fetchType);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponsesForQuestionInSection_matchFound_success() {
        Section section1 = testDataBundle.sections.get("section1InCourse1");
        Section section2 = testDataBundle.sections.get("section2InCourse1");
        FeedbackQuestion question1 = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        ______TS("Match giver section 1 for Q1");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.GIVER;
        List<FeedbackResponse> expected = List.of(
                testDataBundle.feedbackResponses.get("response1ForQ1"),
                testDataBundle.feedbackResponses.get("response2ForQ1"),
                testDataBundle.feedbackResponses.get("response3ForQ1")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForQuestionInSection(question1.getId(),
                section1.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match recipient section 2 for Q1");
        fetchType = FeedbackResultFetchType.RECEIVER;
        expected = List.of(
                testDataBundle.feedbackResponses.get("response3ForQ1"),
                testDataBundle.feedbackResponses.get("response4ForQ1")
        );
        actual = frDb.getFeedbackResponsesForQuestionInSection(question1.getId(), section2.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match both giver and recipient section 2 for Q1");
        fetchType = FeedbackResultFetchType.BOTH;
        expected = List.of(
                testDataBundle.feedbackResponses.get("response4ForQ1")
        );
        actual = frDb.getFeedbackResponsesForQuestionInSection(question1.getId(), section2.getName(), fetchType);
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
                sessionWithResponses.getCourse().getId());
        assertListResponsesEqual(expected, actual);

        ______TS("Session has no responses");
        FeedbackSession sessionWithoutResponses = testDataBundle.feedbackSessions.get(
                "unpublishedSession1InTypicalCourse");
        actual = frDb.getFeedbackResponsesForSession(sessionWithoutResponses, sessionWithResponses.getCourse().getId());
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
