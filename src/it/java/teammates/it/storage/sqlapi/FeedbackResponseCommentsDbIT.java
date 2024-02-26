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

    @Test
    public void testGetFeedbackResponseCommentsForSession_sessionExists_success() {
        Course course = typicalDataBundle.courses.get("course1");

        ______TS("Session with comments");
        FeedbackSession sessionWithComments = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        // All the comments in the typical bundle are for session 1 of course 1
        List<FeedbackResponseComment> expectedComments = new ArrayList<>(typicalDataBundle.feedbackResponseComments.values());
        List<FeedbackResponseComment> results1 = frcDb.getFeedbackResponseCommentsForSession(course.getId(), sessionWithComments.getName());
        assertListCommentsEqual(expectedComments, results1);

        ______TS("Session without comments");
        FeedbackSession sessionWithoutComments = typicalDataBundle.feedbackSessions.get("session2InTypicalCourse");
        List<FeedbackResponseComment> results2 = frcDb.getFeedbackResponseCommentsForSession(course.getId(), sessionWithoutComments.getName());
        assertEquals(0, results2.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForSession_sessionNotFound_shouldReturnEmptyList() {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession session = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        ______TS("Course not found");
        List<FeedbackResponseComment> results1 = frcDb.getFeedbackResponseCommentsForSession("not_exist", session.getName());
        assertEquals(0, results1.size());

        ______TS("Session not found");
        List<FeedbackResponseComment> results2 = frcDb.getFeedbackResponseCommentsForSession(course.getId(), "Nonexistent session");
        assertEquals(0, results2.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestion_questionExists_success() {
        ______TS("Question with comments");
        FeedbackQuestion questionWithComments = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponseComment> expectedComments = Arrays.asList(
                typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1"),
                typicalDataBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1"),
                typicalDataBundle.feedbackResponseComments.get("comment2ToResponse2ForQ1"));
        List<FeedbackResponseComment> results1 = frcDb.getFeedbackResponseCommentsForQuestion(questionWithComments.getId());
        assertListCommentsEqual(expectedComments, results1);

        ______TS("Question without comments");
        FeedbackQuestion questionWithoutComments = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        List<FeedbackResponseComment> results2 = frcDb.getFeedbackResponseCommentsForQuestion(questionWithoutComments.getId());
        assertEquals(0, results2.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestion_questionNotFound_shouldReturnEmptyList() {
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        List<FeedbackResponseComment> results = frcDb.getFeedbackResponseCommentsForQuestion(nonexistentQuestionId);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForSessionInSection_sessionExists_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession session = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion question1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        Section section1 = new Section(course, "Section 1");
        Section section2 = new Section(course, "Section 2");
        HibernateUtil.persist(section1);
        HibernateUtil.persist(section2);
        FeedbackResponse frG1R1 = new FeedbackTextResponse(question1, "", section1, "", section1,
                new FeedbackTextResponseDetails(""));
        FeedbackResponse frG1R2 = new FeedbackTextResponse(question1, "", section1, "", section2,
                new FeedbackTextResponseDetails(""));
        FeedbackResponse frG2R2 = new FeedbackTextResponse(question1, "", section2, "", section2,
                new FeedbackTextResponseDetails(""));
        HibernateUtil.persist(frG1R1);
        HibernateUtil.persist(frG1R2);
        HibernateUtil.persist(frG2R2);
        FeedbackResponseComment frcG1R1 = new FeedbackResponseComment(
                frG1R1, "", FeedbackParticipantType.STUDENTS, section1, section1,
                "", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG1R2 = new FeedbackResponseComment(
                frG1R2, "", FeedbackParticipantType.STUDENTS, section1, section2,
                "", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG2R2 = new FeedbackResponseComment(
                frG2R2, "", FeedbackParticipantType.STUDENTS, section2, section2,
                "", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        HibernateUtil.persist(frcG1R1);
        HibernateUtil.persist(frcG1R2);
        HibernateUtil.persist(frcG2R2);

        ______TS("Section 1 match");
        List<FeedbackResponseComment> results1 = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session.getName(), section1.getName());
        assertEquals(7, results1.size()); // including typical bundle
        assertTrue(results1.contains(frcG1R1));
        assertTrue(results1.contains(frcG1R2));
        assertFalse(results1.contains(frcG2R2));

        ______TS("Section 2 match");
        List<FeedbackResponseComment> results2 = frcDb.getFeedbackResponseCommentsForSessionInSection(
                course.getId(), session.getName(), section2.getName());
        assertEquals(2, results2.size()); // including typical bundle
        assertFalse(results2.contains(frcG1R1));
        assertTrue(results2.contains(frcG1R2));
        assertTrue(results2.contains(frcG2R2));
    }

    @Test
    public void testGetFeedbackResponseCommentsForSessionInSection_sessionOrSectionNotFound_shouldReturnEmptyList() {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession session = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Section section = typicalDataBundle.sections.get("section1InCourse1");

        ______TS("Course not found");
        List<FeedbackResponseComment> results1 = frcDb.getFeedbackResponseCommentsForSessionInSection("not_exist", session.getName(), section.getName());
        assertEquals(0, results1.size());

        ______TS("Session not found");
        List<FeedbackResponseComment> results2 = frcDb.getFeedbackResponseCommentsForSessionInSection(course.getId(), "Nonexistent session", section.getName());
        assertEquals(0, results2.size());

        ______TS("Section not found");
        List<FeedbackResponseComment> results3 = frcDb.getFeedbackResponseCommentsForSessionInSection(course.getId(), session.getName(), "Nonexistent section");
        assertEquals(0, results3.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_questionExists_success() {
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackQuestion question1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion question2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        Section section1 = new Section(course, "Section 1");
        Section section2 = new Section(course, "Section 2");
        HibernateUtil.persist(section1);
        HibernateUtil.persist(section2);
        FeedbackResponse frG1R1Q1 = new FeedbackTextResponse(question1, "", section1, "", section1,
                new FeedbackTextResponseDetails(""));
        FeedbackResponse frG1R2Q2 = new FeedbackTextResponse(question2, "", section1, "", section2,
                new FeedbackTextResponseDetails(""));
        FeedbackResponse frG1R2Q1 = new FeedbackTextResponse(question1, "", section1, "", section2,
                new FeedbackTextResponseDetails(""));
        FeedbackResponse frG2R2Q1 = new FeedbackTextResponse(question1, "", section2, "", section2,
                new FeedbackTextResponseDetails(""));
        HibernateUtil.persist(frG1R1Q1);
        HibernateUtil.persist(frG1R2Q2);
        HibernateUtil.persist(frG1R2Q1);
        HibernateUtil.persist(frG2R2Q1);
        FeedbackResponseComment frcG1R1Q1 = new FeedbackResponseComment(
                frG1R1Q1, "", FeedbackParticipantType.STUDENTS, section1, section1,
                "", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG1R2Q2 = new FeedbackResponseComment(
                frG1R2Q2, "", FeedbackParticipantType.STUDENTS, section1, section1,
                "", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG1R2Q1 = new FeedbackResponseComment(
                frG1R2Q1, "", FeedbackParticipantType.STUDENTS, section1, section2,
                "", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        FeedbackResponseComment frcG2R2Q1 = new FeedbackResponseComment(
                frG2R2Q1, "", FeedbackParticipantType.STUDENTS, section2, section2,
                "", true, true, new ArrayList<>(), new ArrayList<>(), ""
        );
        HibernateUtil.persist(frcG1R1Q1);
        HibernateUtil.persist(frcG1R2Q2);
        HibernateUtil.persist(frcG1R2Q1);
        HibernateUtil.persist(frcG2R2Q1);

        ______TS("Section 1 Question 1 match");
        List<FeedbackResponseComment> results1 = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question1.getId(), section1.getName());
        assertEquals(5, results1.size()); // including comments in typical bundle
        assertTrue(results1.contains(frcG1R1Q1));
        assertTrue(results1.contains(frcG1R2Q1));
        assertFalse(results1.contains(frcG1R2Q2));
        assertFalse(results1.contains(frcG2R2Q1));

        ______TS("Section 2 Question 1 match");
        List<FeedbackResponseComment> results2 = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question1.getId(), section2.getName());
        assertEquals(2, results2.size()); // including comments in typical bundle
        assertFalse(results2.contains(frcG1R1Q1));
        assertTrue(results2.contains(frcG1R2Q1));
        assertFalse(results2.contains(frcG1R2Q2));
        assertTrue(results2.contains(frcG2R2Q1));

        ______TS("Section 1 Question 2 match");
        List<FeedbackResponseComment> results3 = frcDb.getFeedbackResponseCommentsForQuestionInSection(
                question2.getId(), section1.getName());
        assertEquals(2, results3.size()); // including comments in typical bundle
        assertFalse(results3.contains(frcG1R1Q1));
        assertFalse(results3.contains(frcG1R2Q1));
        assertTrue(results3.contains(frcG1R2Q2));
        assertFalse(results3.contains(frcG2R2Q1));
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_questionOrSectionNotFound_shouldReturnEmptyList() {
        Section section = typicalDataBundle.sections.get("section1InCourse1");
        FeedbackQuestion question = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
 
        ______TS("Question not found");
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        List<FeedbackResponseComment> results1 = frcDb.getFeedbackResponseCommentsForQuestionInSection(nonexistentQuestionId, section.getName());
        assertEquals(0, results1.size());

        ______TS("Section not found");
        List<FeedbackResponseComment> results2 = frcDb.getFeedbackResponseCommentsForQuestionInSection(question.getId(), "Nonexistent section");
        assertEquals(0, results2.size());
    }

    private void assertListCommentsEqual(List<FeedbackResponseComment> a, List<FeedbackResponseComment> b) {
        assertEquals(a.size(), b.size());
        assertTrue(
                String.format("List contents are not equal.\nExpected: %s,\nActual: %s", a.toString(), b.toString()),
                new HashSet<>(a).equals(new HashSet<>(b)));
    }
}
