package teammates.it.storage.sqlapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlDataBundle;
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

/**
 * SUT: {@link FeedbackResponseCommentsDb}.
 */
public class FeedbackResponseCommentsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

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
    public void testGetFeedbackResponseCommentForResponseFromParticipant() {
        ______TS("success: typical case");
        FeedbackResponse fr = testDataBundle.feedbackResponses.get("response1ForQ1");

        FeedbackResponseComment expectedComment = testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        FeedbackResponseComment actualComment = frcDb.getFeedbackResponseCommentForResponseFromParticipant(fr.getId());

        assertEquals(expectedComment, actualComment);
    }

    private FeedbackResponseComment prepareSqlInjectionTest() {
        FeedbackResponseComment frc = testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
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

        FeedbackResponse fr = testDataBundle.feedbackResponses.get("response1ForQ1");
        Section s = testDataBundle.sections.get("section2InCourse1");

        String sqli = "'');/**/DELETE/**/FROM/**/feedback_response_comments;--@gmail.com";
        FeedbackResponseComment newFrc = new FeedbackResponseComment(
                fr, "", FeedbackParticipantType.INSTRUCTORS, s, s, "",
                false, false,
                new ArrayList<>(), new ArrayList<>(), sqli);

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
        Course course = testDataBundle.courses.get("course1");

        ______TS("Session with comments");
        FeedbackSession sessionWithComments = testDataBundle.feedbackSessions.get("session1InCourse1");
        List<FeedbackResponseComment> expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse2ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ2s"),
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ3"),
                testDataBundle.feedbackResponseComments.get("comment1ToResponse4ForQ1")
        );
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSession(
                        course.getId(), sessionWithComments.getName());
        assertListCommentsEqual(expected, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForSession_matchNotFound_shouldReturnEmptyList() {
        Course course = testDataBundle.courses.get("course1");
        FeedbackSession session = testDataBundle.feedbackSessions.get("session1InCourse1");

        ______TS("Course not found");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSession("not_exist", session.getName());
        assertEquals(0, results.size());

        ______TS("Session not found");
        results = frcDb.getFeedbackResponseCommentsForSession(course.getId(), "Nonexistent session");
        assertEquals(0, results.size());

        ______TS("Session without comments");
        FeedbackSession sessionWithoutComments = testDataBundle.feedbackSessions.get("ongoingSession1InCourse1");
        results = frcDb.getFeedbackResponseCommentsForSession(course.getId(), sessionWithoutComments.getName());
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestion_matchFound_success() {
        ______TS("Question with comments");
        FeedbackQuestion questionWithComments = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponseComment> expectedComments = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse2ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment1ToResponse4ForQ1")
        );
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
        FeedbackQuestion questionWithoutComments = testDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        results = frcDb.getFeedbackResponseCommentsForQuestion(questionWithoutComments.getId());
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForSessionInSection_matchFound_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Section section1 = testDataBundle.sections.get("section1InCourse1");
        Section section2 = testDataBundle.sections.get("section2InCourse1");
        Course course = testDataBundle.courses.get("course1");
        FeedbackSession session1 = testDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSession session2 = testDataBundle.feedbackSessions.get("session2InTypicalCourse");

        ______TS("Section 1 Session 2 match");
        List<FeedbackResponseComment> expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1InSession2")
        );
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session2.getName(), section1.getName());
        assertListCommentsEqual(expected, results);

        ______TS("Section 2 Session 1 match");
        expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse4ForQ1")
        );
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session1.getName(), section2.getName());
        assertListCommentsEqual(expected, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForSessionInSection_matchNotFound_shouldReturnEmptyList() {
        Course course = testDataBundle.courses.get("course1");
        FeedbackSession session1 = testDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSession session2 = testDataBundle.feedbackSessions.get("session2InTypicalCourse");
        Section section1 = testDataBundle.sections.get("section1InCourse1");
        Section section2 = testDataBundle.sections.get("section2InCourse1");

        ______TS("Course not found");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                "not_exist", session1.getName(), section1.getName());
        assertEquals(0, results.size());

        ______TS("Session not found");
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), "Nonexistent session", section1.getName());
        assertEquals(0, results.size());

        ______TS("Section not found");
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session1.getName(), "Nonexistent section");
        assertEquals(0, results.size());

        ______TS("No matching comments exist");
        results = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session2.getName(), section2.getName());
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_matchFound_success() {
        Section section1 = testDataBundle.sections.get("section1InCourse1");
        Section section2 = testDataBundle.sections.get("section2InCourse1");
        FeedbackQuestion question1 = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion question2 = testDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");

        ______TS("Section 1 Question 1 match");
        List<FeedbackResponseComment> expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment2ToResponse2ForQ1"),
                testDataBundle.feedbackResponseComments.get("comment1ToResponse4ForQ1")
        );
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question1.getId(), section1.getName());
        assertListCommentsEqual(expected, results);

        ______TS("Section 2 Question 1 match");
        expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse4ForQ1")
        );
        results = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question1.getId(), section2.getName());
        assertListCommentsEqual(expected, results);

        ______TS("Section 1 Question 2 match");
        expected = List.of(
                testDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ2s")
        );
        results = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question2.getId(), section1.getName());
        assertListCommentsEqual(expected, results);
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_matchNotFound_shouldReturnEmptyList() {
        Section section = testDataBundle.sections.get("section1InCourse1");
        FeedbackQuestion question1 = testDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion question2 = testDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");

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

}
