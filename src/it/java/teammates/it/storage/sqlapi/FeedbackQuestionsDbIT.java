package teammates.it.storage.sqlapi;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.questions.FeedbackConstantSumQuestion;

/**
 * SUT: {@link FeedbackQuestionsDb}.
 */
public class FeedbackQuestionsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackQuestionsDb fqDb = FeedbackQuestionsDb.inst();

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
    public void testGetFeedbackQuestionsForSession() {
        ______TS("success: typical case");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        FeedbackQuestion fq6 = typicalDataBundle.feedbackQuestions.get("qn6InSession1InCourse1NoResponses");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4, fq5, fq6);

        List<FeedbackQuestion> actualQuestions = fqDb.getFeedbackQuestionsForSession(fs.getId());

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testGetFeedbackQuestionsForGiverType() {
        ______TS("success: typical case");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2);

        List<FeedbackQuestion> actualQuestions = fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.STUDENTS);

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testHasFeedbackQuestionsForGiverType() {
        ______TS("success: typical case");
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        boolean actual = fqDb.hasFeedbackQuestionsForGiverType(
                fs.getName(), course.getId(), FeedbackParticipantType.STUDENTS);

        assertTrue(actual);
    }

    private FeedbackQuestion prepareSqlInjectionTests() {
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        fqDb.createFeedbackQuestion(fq);

        // Ensure feedback_questions db has at least 1 entry / row.
        assertNotNull(fqDb.getFeedbackQuestion(fq.getId()));

        return fq;
    }

    @Test
    public void testSqlInjectionInCreateFeedbackQuestion() {
        prepareSqlInjectionTests();

        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        String sqli = "', 'FeedbackTextQuestion', 329c23fd-10de-4c47-8128-115df68ba758)); DELETE FROM feedback_questions;--";

        FeedbackQuestion fq = new FeedbackConstantSumQuestion(
                fs, 1, "", FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.STUDENTS,
                1, new ArrayList<FeedbackParticipantType>(), new ArrayList<FeedbackParticipantType>(),
                new ArrayList<FeedbackParticipantType>(), new FeedbackConstantSumQuestionDetails(sqli)
        );

        fqDb.createFeedbackQuestion(fq);

        // If SQLi is successful, feedback questions would have been deleted from db. So get will return null.
        assertNotNull(fqDb.getFeedbackQuestion(fq.getId()));
    }

    @Test
    public void testSqlInjectionInHasFeedbackQuestionsForGiverType() throws Exception {
        FeedbackQuestion fq = prepareSqlInjectionTests();

        String sessionName = "'; DELETE FROM feedback_questions;--";
        fqDb.hasFeedbackQuestionsForGiverType(sessionName, fq.getCourseId(), FeedbackParticipantType.INSTRUCTORS);

        // If SQLi is successful, feedback questions would have been deleted from db. So get will return null.
        assertNotNull(fqDb.getFeedbackQuestion(fq.getId()));
    }

}
