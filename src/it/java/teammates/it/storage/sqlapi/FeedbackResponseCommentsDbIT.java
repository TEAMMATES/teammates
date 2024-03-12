package teammates.it.storage.sqlapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.responses.FeedbackTextResponse;

/**
 * SUT: {@link FeedbackResponseCommentsDb}.
 */
public class FeedbackResponseCommentsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

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
    }

    @Test
    public void testGetFeedbackResponseCommentForResponseFromParticipant() {
        ______TS("success: typical case");
        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");

        FeedbackResponseComment expectedComment = typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        FeedbackResponseComment actualComment = frcDb.getFeedbackResponseCommentForResponseFromParticipant(fr.getId());

        assertEquals(expectedComment, actualComment);
    }

    private FeedbackResponseComment prepareSqlInjectionTest() {
        FeedbackResponseComment frc = typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        assertNotNull(frcDb.getFeedbackResponseComment(frc.getId()));

        return frc;
    }

    private void checkSqlInjectionFailed(FeedbackResponseComment frc) {
        assertNotNull(frcDb.getFeedbackResponseComment(frc.getId()));
    }

    @Test
    public void testSqlInjectionInUpdateGiverEmailOfFeedbackResponseComments() {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        String sqli = "'; DELETE FROM feedback_response_comments;--";
        frcDb.updateGiverEmailOfFeedbackResponseComments(sqli, "", "");

        checkSqlInjectionFailed(frc);
    }

    @Test
    public void testSqlInjectionInUpdateLastEditorEmailOfFeedbackResponseComments() {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        String sqli = "'; DELETE FROM feedback_response_comments;--";
        frcDb.updateLastEditorEmailOfFeedbackResponseComments(sqli, "", "");

        checkSqlInjectionFailed(frc);
    }

    @Test
    public void testSqlInjectionInCreateFeedbackResponseComment() throws Exception {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        Section s = typicalDataBundle.sections.get("section2InCourse1");

        String sqli = "'');/**/DELETE/**/FROM/**/feedback_response_comments;--@gmail.com";
        FeedbackResponseComment newFrc = new FeedbackResponseComment(
                fr, "", FeedbackParticipantType.INSTRUCTORS, s, s, "",
                false, false,
                new ArrayList<FeedbackParticipantType>(), new ArrayList<FeedbackParticipantType>(), sqli);

        frcDb.createFeedbackResponseComment(newFrc);

        checkSqlInjectionFailed(frc);
    }

    @Test
    public void testSqlInjectionInUpdateFeedbackResponseComment() throws Exception {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        String sqli = "'');/**/DELETE/**/FROM/**/feedback_response_comments;--@gmail.com";
        frc.setLastEditorEmail(sqli);
        frcDb.updateFeedbackResponseComment(frc);

        checkSqlInjectionFailed(frc);
    }

    @Test
    public void testGetFeedbackResponseCommentsForSession_matchFound_success() {
        Course course = typicalDataBundle.courses.get("course1");

        ______TS("Session with comments");
        FeedbackSession sessionWithComments = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        List<FeedbackResponseComment> expected = List.of(
                typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                typicalDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1"),
                typicalDataBundle.feedbackResponseComments.get("comment2ToResponse2ForQ1"),
                typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ2s"),
                typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ3")
        );
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSession(
                        course.getId(), sessionWithComments.getName());
        assertListCommentsEqual(expected, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForSession_matchNotFound_shouldReturnEmptyList() {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession session = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        ______TS("Course not found");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSession("not_exist", session.getName());
        assertEquals(0, results.size());

        ______TS("Session not found");
        results = frcDb.getFeedbackResponseCommentsForSession(course.getId(), "Nonexistent session");
        assertEquals(0, results.size());

        ______TS("Session without comments");
        FeedbackSession sessionWithoutComments = typicalDataBundle.feedbackSessions.get("session2InTypicalCourse");
        results = frcDb.getFeedbackResponseCommentsForSession(course.getId(), sessionWithoutComments.getName());
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestion_matchFound_success() {
        ______TS("Question with comments");
        FeedbackQuestion questionWithComments = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponseComment> expectedComments = Arrays.asList(
                typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                typicalDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1"),
                typicalDataBundle.feedbackResponseComments.get("comment2ToResponse2ForQ1"));
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForQuestion(questionWithComments.getId());
        assertListCommentsEqual(expectedComments, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestion_matchNotFound_shouldReturnEmptyList() {
        ______TS("Question not found");
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForQuestion(nonexistentQuestionId);
        assertEquals(0, results.size());

        ______TS("Question without comments");
        FeedbackQuestion questionWithoutComments = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        results = frcDb.getFeedbackResponseCommentsForQuestion(questionWithoutComments.getId());
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForSessionInSection_matchFound_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        SqlDataBundle additionalTestData = getAdditionalTestData();
        Section section1 = additionalTestData.sections.get("section1aInCourse1");
        Section section2 = additionalTestData.sections.get("section2aInCourse1");
        Course course = additionalTestData.courses.get("course1");
        FeedbackSession session = additionalTestData.feedbackSessions.get("session1InCourse1");

        ______TS("Section 1A match");
        List<FeedbackResponseComment> expected = List.of(
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS1aToS1a"),
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS1aToS2a"),
                additionalTestData.feedbackResponseComments.get("commentForQ2FromS1aToS2a")
        );
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session.getName(), section1.getName());
        assertListCommentsEqual(expected, results);

        ______TS("Section 2A match");
        expected = List.of(
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS1aToS2a"),
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS2aToS2a"),
                additionalTestData.feedbackResponseComments.get("commentForQ2FromS1aToS2a")
        );
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session.getName(), section2.getName());
        assertListCommentsEqual(expected, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForSessionInSection_matchNotFound_shouldReturnEmptyList() {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession session1 = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSession session2 = typicalDataBundle.feedbackSessions.get("session2InTypicalCourse");
        Section section = typicalDataBundle.sections.get("section1InCourse1");

        ______TS("Course not found");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                "not_exist", session1.getName(), section.getName());
        assertEquals(0, results.size());

        ______TS("Session not found");
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), "Nonexistent session", section.getName());
        assertEquals(0, results.size());

        ______TS("Section not found");
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session1.getName(), "Nonexistent section");
        assertEquals(0, results.size());

        ______TS("No matching comments exist");
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session2.getName(), section.getName());
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_matchFound_success() {
        SqlDataBundle additionalTestData = getAdditionalTestData();
        Section section1 = additionalTestData.sections.get("section1aInCourse1");
        Section section2 = additionalTestData.sections.get("section2aInCourse1");
        FeedbackQuestion question1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion question2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");

        ______TS("Section 1A Question 1 match");
        List<FeedbackResponseComment> expected = List.of(
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS1aToS1a"),
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS1aToS2a")
        );
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question1.getId(), section1.getName());
        assertListCommentsEqual(expected, results);

        ______TS("Section 2A Question 1 match");
        expected = List.of(
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS1aToS2a"),
                additionalTestData.feedbackResponseComments.get("commentForQ1FromS2aToS2a")
        );
        results = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question1.getId(), section2.getName());
        assertListCommentsEqual(expected, results);

        ______TS("Section 1 Question 2 match");
        expected = List.of(
                additionalTestData.feedbackResponseComments.get("commentForQ2FromS1aToS2a")
        );
        results = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question2.getId(), section1.getName());
        assertListCommentsEqual(expected, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_matchNotFound_shouldReturnEmptyList() {
        Section section = typicalDataBundle.sections.get("section1InCourse1");
        FeedbackQuestion question1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion question2 = typicalDataBundle.feedbackQuestions.get("qn1InSession2InCourse1");

        ______TS("Question not found");
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                nonexistentQuestionId, section.getName());
        assertEquals(0, results.size());

        ______TS("Section not found");
        results = frcDb.getFeedbackResponseCommentsForQuestionInSection(question1.getId(), "Nonexistent section");
        assertEquals(0, results.size());

        ______TS("No matching comments exist");
        results = frcDb.getFeedbackResponseCommentsForQuestionInSection(question2.getId(), section.getName());
        assertEquals(0, results.size());
    }

    private void assertListCommentsEqual(List<FeedbackResponseComment> expected, List<FeedbackResponseComment> actual) {
        assertTrue(
                String.format("List contents are not equal.%nExpected: %s,%nActual: %s",
                        expected.toString(), actual.toString()),
                new HashSet<>(expected).equals(new HashSet<>(actual)));
        assertEquals("List size not equal.", expected.size(), actual.size());
    }

    /**
     * Generate extra test comments not included in typical bundle.
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
        FeedbackResponse frG1R2Q2 = new FeedbackTextResponse(question2, "", section1a, "", section2a,
                        new FeedbackTextResponseDetails("Response Q2 S1 to S2"));
        FeedbackResponse frG2R2Q1 = new FeedbackTextResponse(question1, "", section2a, "", section2a,
                new FeedbackTextResponseDetails("Response Q1 S2 to S2"));
        FeedbackResponseComment frcG1R1Q1 = new FeedbackResponseComment(
                frG1R1Q1, "", FeedbackParticipantType.STUDENTS, section1a, section1a,
                "Comment Q1 S1 to S1", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG1R2Q1 = new FeedbackResponseComment(
                frG1R2Q1, "", FeedbackParticipantType.STUDENTS, section1a, section2a,
                "Comment Q1 S1 to S2", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG1R2Q2 = new FeedbackResponseComment(
                frG1R2Q2, "", FeedbackParticipantType.STUDENTS, section1a, section2a,
                "Comment Q2 S1 to S2", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG2R2Q1 = new FeedbackResponseComment(
                frG2R2Q1, "", FeedbackParticipantType.STUDENTS, section2a, section2a,
                "Comment Q1 S2 to S2", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );

        HibernateUtil.persist(section1a);
        HibernateUtil.persist(section2a);
        course.addSection(section1a);
        course.addSection(section2a);
        HibernateUtil.merge(session);
        HibernateUtil.merge(course);
        HibernateUtil.persist(frG1R1Q1);
        HibernateUtil.persist(frG1R2Q2);
        HibernateUtil.persist(frG1R2Q1);
        HibernateUtil.persist(frG2R2Q1);
        HibernateUtil.persist(frcG1R1Q1);
        HibernateUtil.persist(frcG1R2Q2);
        HibernateUtil.persist(frcG1R2Q1);
        HibernateUtil.persist(frcG2R2Q1);

        bundle.courses.put("course1", course);
        bundle.feedbackSessions.put("session1InCourse1", session);
        bundle.sections.put("section1aInCourse1", section1a);
        bundle.sections.put("section2aInCourse1", section2a);
        bundle.feedbackResponses.put("responseForQ1FromS1aToS1a", frG1R1Q1);
        bundle.feedbackResponses.put("responseForQ1FromS1aToS2a", frG1R2Q1);
        bundle.feedbackResponses.put("responseForQ2FromS1aToS2a", frG1R2Q2);
        bundle.feedbackResponses.put("responseForQ1FromS2aToS2a", frG2R2Q1);
        bundle.feedbackResponseComments.put("commentForQ1FromS1aToS1a", frcG1R1Q1);
        bundle.feedbackResponseComments.put("commentForQ1FromS1aToS2a", frcG1R2Q1);
        bundle.feedbackResponseComments.put("commentForQ2FromS1aToS2a", frcG1R2Q2);
        bundle.feedbackResponseComments.put("commentForQ1FromS2aToS2a", frcG2R2Q1);

        return bundle;
    }
}
