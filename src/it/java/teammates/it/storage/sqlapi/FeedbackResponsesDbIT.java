package teammates.it.storage.sqlapi;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SqlDataBundle;
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

    private SqlDataBundle typicalDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalDataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");

        List<FeedbackResponse> expectedQuestions = List.of(fr);

        List<FeedbackResponse> actualQuestions =
                frDb.getFeedbackResponsesFromGiverForQuestion(fq.getId(), "student1@teammates.tmt");

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade() {
        ______TS("success: typical case");
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponse fr1 = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponse fr2 = typicalDataBundle.feedbackResponses.get("response2ForQ1");
        FeedbackResponseComment frc1 = typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");

        frDb.deleteFeedbackResponsesForQuestionCascade(fq.getId());

        assertNull(frDb.getFeedbackResponse(fr1.getId()));
        assertNull(frDb.getFeedbackResponse(fr2.getId()));
        assertNull(frcDb.getFeedbackResponseComment(frc1.getId()));
    }

    @Test
    public void testDeleteFeedback() {
        ______TS("success: typical case");
        FeedbackResponse fr1 = typicalDataBundle.feedbackResponses.get("response1ForQ1");

        frDb.deleteFeedbackResponse(fr1);

        assertNull(frDb.getFeedbackResponse(fr1.getId()));
    }

    @Test
    public void testHasResponsesFromGiverInSession() {
        ______TS("success: typical case");
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

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
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        boolean actualResponse1 =
                frDb.areThereResponsesForQuestion(fq1.getId());

        assertTrue(actualResponse1);

        ______TS("feedback question with no responses");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn6InSession1InCourse1NoResponses");

        boolean actualResponse2 =
                frDb.areThereResponsesForQuestion(fq2.getId());

        assertFalse(actualResponse2);
    }

    @Test
    public void testHasResponsesForCourse() {
        ______TS("success: typical case");
        Course course = typicalDataBundle.courses.get("course1");

        boolean actual =
                frDb.hasResponsesForCourse(course.getId());

        assertTrue(actual);
    }

    @Test
    public void testGetFeedbackResponsesForRecipientForQuestion_matchNotFound_shouldReturnEmptyList() {
        ______TS("Question not found");
        String recipient = "student1@teammates.tmt";
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        List<FeedbackResponse> results = frDb.getFeedbackResponsesForRecipientForQuestion(nonexistentQuestionId, recipient);
        assertEquals(0, results.size());

        ______TS("No matching responses exist");
        FeedbackQuestion questionWithNoResponses = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        results = frDb.getFeedbackResponsesForRecipientForQuestion(questionWithNoResponses.getId(), recipient);
        assertEquals(0, results.size());

    }

    @Test
    public void testGetFeedbackResponsesForRecipientForQuestion_matchFound_success() {
        ______TS("Matching responses exist");
        String recipient = "student2@teammates.tmt";
        FeedbackQuestion question = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponse> expected = List.of(
                typicalDataBundle.feedbackResponses.get("response2ForQ1")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForRecipientForQuestion(question.getId(), recipient);
        assertListResponsesEqual(expected, actual);

    }

    @Test
    public void testGetFeedbackResponsesForSessionInSection_matchNotFound_shouldReturnEmptyList() {
        String section2 = typicalDataBundle.sections.get("section2InCourse1").getName();
        FeedbackSession session = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        String courseId = session.getCourse().getId();

        ______TS("No matching responses exist for giver section");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.GIVER;
        List<FeedbackResponse> results = frDb.getFeedbackResponsesForSessionInSection(
                session, courseId, section2, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for recipient section");
        fetchType = FeedbackResultFetchType.RECEIVER;
        results = frDb.getFeedbackResponsesForSessionInSection(session, courseId, section2, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for both giver and recipient section");
        fetchType = FeedbackResultFetchType.BOTH;
        results = frDb.getFeedbackResponsesForSessionInSection(session, courseId, section2, fetchType);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponsesForSessionInSection_matchFound_success() {
        SqlDataBundle additionalTestData = getAdditionalTestData();
        FeedbackSession session = additionalTestData.feedbackSessions.get("session1InCourse1");
        Course course = additionalTestData.courses.get("course1");
        Section section1a = additionalTestData.sections.get("section1aInCourse1");
        Section section2a = additionalTestData.sections.get("section2aInCourse1");

        ______TS("Match giver section 1a");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.GIVER;
        List<FeedbackResponse> expected = List.of(
                additionalTestData.feedbackResponses.get("responseForQ1FromS1aToS1a"),
                additionalTestData.feedbackResponses.get("responseForQ2FromS1aToS1a"),
                additionalTestData.feedbackResponses.get("responseForQ1FromS1aToS2a"),
                additionalTestData.feedbackResponses.get("responseForQ2FromS1aToS2a")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForSessionInSection(
                session, course.getId(), section1a.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match recipient section 2a");
        fetchType = FeedbackResultFetchType.RECEIVER;
        expected = List.of(
                additionalTestData.feedbackResponses.get("responseForQ1FromS2aToS2a"),
                additionalTestData.feedbackResponses.get("responseForQ2FromS2aToS2a"),
                additionalTestData.feedbackResponses.get("responseForQ1FromS1aToS2a"),
                additionalTestData.feedbackResponses.get("responseForQ2FromS1aToS2a")
        );
        actual = frDb.getFeedbackResponsesForSessionInSection(session, course.getId(),
                section2a.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match both giver and recipient section 2a");
        fetchType = FeedbackResultFetchType.BOTH;
        expected = List.of(
                additionalTestData.feedbackResponses.get("responseForQ1FromS2aToS2a"),
                additionalTestData.feedbackResponses.get("responseForQ2FromS2aToS2a")
        );
        actual = frDb.getFeedbackResponsesForSessionInSection(session, course.getId(),
                section2a.getName(), fetchType);
        assertListResponsesEqual(expected, actual);
    }

    @Test
    public void testGetFeedbackResponsesForQuestionInSection_matchNotFound_shouldReturnEmptyList() {
        String section1 = typicalDataBundle.sections.get("section1InCourse1").getName();
        String section2 = typicalDataBundle.sections.get("section2InCourse1").getName();

        ______TS("Question not found");
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.BOTH;
        List<FeedbackResponse> results = frDb.getFeedbackResponsesForQuestionInSection(nonexistentQuestionId,
                section1, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for giver section");
        UUID questionId = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1").getId();
        fetchType = FeedbackResultFetchType.GIVER;
        results = frDb.getFeedbackResponsesForQuestionInSection(questionId, section2, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for recipient section");
        fetchType = FeedbackResultFetchType.RECEIVER;
        results = frDb.getFeedbackResponsesForQuestionInSection(questionId, section2, fetchType);
        assertEquals(0, results.size());

        ______TS("No matching responses exist for both giver and recipient section");
        fetchType = FeedbackResultFetchType.BOTH;
        results = frDb.getFeedbackResponsesForQuestionInSection(questionId, section2, fetchType);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponsesForQuestionInSection_matchFound_success() {
        SqlDataBundle additionalTestData = getAdditionalTestData();
        Section section1a = additionalTestData.sections.get("section1aInCourse1");
        Section section2a = additionalTestData.sections.get("section2aInCourse1");
        FeedbackQuestion question1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion question2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");

        ______TS("Match giver section 1a for Q1");
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.GIVER;
        List<FeedbackResponse> expected = List.of(
                additionalTestData.feedbackResponses.get("responseForQ1FromS1aToS1a"),
                additionalTestData.feedbackResponses.get("responseForQ1FromS1aToS2a")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForQuestionInSection(question1.getId(),
                section1a.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match recipient section 2a for Q1");
        fetchType = FeedbackResultFetchType.RECEIVER;
        expected = List.of(
                additionalTestData.feedbackResponses.get("responseForQ1FromS2aToS2a"),
                additionalTestData.feedbackResponses.get("responseForQ1FromS1aToS2a")
        );
        actual = frDb.getFeedbackResponsesForQuestionInSection(question1.getId(), section2a.getName(), fetchType);
        assertListResponsesEqual(expected, actual);

        ______TS("Match both giver and recipient section 1a for Q2");
        fetchType = FeedbackResultFetchType.BOTH;
        expected = List.of(
                additionalTestData.feedbackResponses.get("responseForQ2FromS1aToS1a")
        );
        actual = frDb.getFeedbackResponsesForQuestionInSection(question2.getId(), section1a.getName(), fetchType);
        assertListResponsesEqual(expected, actual);
    }

    @Test
    public void testGetFeedbackResponsesForSession() {
        ______TS("Session has responses");
        FeedbackSession sessionWithResponses = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        List<FeedbackResponse> expected = List.of(
                typicalDataBundle.feedbackResponses.get("response1ForQ1"),
                typicalDataBundle.feedbackResponses.get("response2ForQ1"),
                typicalDataBundle.feedbackResponses.get("response1ForQ2"),
                typicalDataBundle.feedbackResponses.get("response2ForQ2"),
                typicalDataBundle.feedbackResponses.get("response1ForQ3")
        );
        List<FeedbackResponse> actual = frDb.getFeedbackResponsesForSession(sessionWithResponses,
                sessionWithResponses.getCourse().getId());
        assertListResponsesEqual(expected, actual);

        ______TS("Session has no responses");
        FeedbackSession sessionWithoutResponses = typicalDataBundle.feedbackSessions.get(
                "unpublishedSession1InTypicalCourse");
        actual = frDb.getFeedbackResponsesForSession(sessionWithoutResponses, sessionWithResponses.getCourse().getId());
        assertEquals(0, actual.size());
    }

    private void assertListResponsesEqual(List<FeedbackResponse> expected, List<FeedbackResponse> actual) {
        assertTrue(
                String.format("List contents are not equal.%nExpected: %s,%nActual: %s",
                        expected.toString(), actual.toString()),
                new HashSet<>(expected).equals(new HashSet<>(actual)));
        assertEquals("List size not equal.", expected.size(), actual.size());
    }

    /**
     * Generate extra test responses not included in typical bundle.
     */
    private SqlDataBundle getAdditionalTestData() {
        SqlDataBundle bundle = new SqlDataBundle();

        Course course = typicalDataBundle.courses.get("course1");
        FeedbackQuestion question1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion question2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackSession session = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Section section1a = new Section(course, "Section 1A");
        Section section2a = new Section(course, "Section 2A");
        FeedbackResponse frG1R1Q1 = new FeedbackTextResponse(question1, "", section1a, "", section1a,
                new FeedbackTextResponseDetails("Response Q1 S1 to S1"));
        FeedbackResponse frG1R2Q1 = new FeedbackTextResponse(question1, "", section1a, "", section2a,
                new FeedbackTextResponseDetails("Response Q1 S1 to S2"));
        FeedbackResponse frG2R2Q1 = new FeedbackTextResponse(question1, "", section2a, "", section2a,
                new FeedbackTextResponseDetails("Response Q1 S2 to S2"));
        FeedbackResponse frG1R1Q2 = new FeedbackTextResponse(question2, "", section1a, "", section1a,
                new FeedbackTextResponseDetails("Response Q2 S1 to S1"));
        FeedbackResponse frG1R2Q2 = new FeedbackTextResponse(question2, "", section1a, "", section2a,
                new FeedbackTextResponseDetails("Response Q2 S1 to S2"));
        FeedbackResponse frG2R2Q2 = new FeedbackTextResponse(question2, "", section2a, "", section2a,
                new FeedbackTextResponseDetails("Response Q2 S2 to S2"));

        HibernateUtil.persist(section1a);
        HibernateUtil.persist(section2a);
        course.addSection(section1a);
        course.addSection(section2a);
        HibernateUtil.merge(course);
        HibernateUtil.persist(frG1R1Q1);
        HibernateUtil.persist(frG1R2Q1);
        HibernateUtil.persist(frG2R2Q1);
        HibernateUtil.persist(frG1R1Q2);
        HibernateUtil.persist(frG1R2Q2);
        HibernateUtil.persist(frG2R2Q2);

        bundle.courses.put("course1", course);
        bundle.feedbackSessions.put("session1InCourse1", session);
        bundle.sections.put("section1aInCourse1", section1a);
        bundle.sections.put("section2aInCourse1", section2a);
        bundle.feedbackResponses.put("responseForQ1FromS1aToS1a", frG1R1Q1);
        bundle.feedbackResponses.put("responseForQ1FromS1aToS2a", frG1R2Q1);
        bundle.feedbackResponses.put("responseForQ1FromS2aToS2a", frG2R2Q1);
        bundle.feedbackResponses.put("responseForQ2FromS1aToS1a", frG1R1Q2);
        bundle.feedbackResponses.put("responseForQ2FromS1aToS2a", frG1R2Q2);
        bundle.feedbackResponses.put("responseForQ2FromS2aToS2a", frG2R2Q2);

        return bundle;
    }
}
