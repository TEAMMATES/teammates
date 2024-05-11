package teammates.it.storage.sqlapi;

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
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

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
    public void testGetFeedbackQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion expectedFq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        FeedbackQuestion actualFq = fqDb.getFeedbackQuestion(expectedFq.getId());

        assertEquals(expectedFq, actualFq);

        ______TS("failure: does not exist, returns null");
        actualFq = fqDb.getFeedbackQuestion(UUID.randomUUID());
        assertNull(actualFq);

        ______TS("failure: null parameter, assertion error");
        assertThrows(AssertionError.class, () -> fqDb.getFeedbackQuestion(null));
    }

    @Test
    public void testCreateFeedbackQuestion() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: typical case");
        FeedbackQuestion expectedFq = getTypicalFeedbackQuestionForSession(
                getTypicalFeedbackSessionForCourse(getTypicalCourse()));

        fqDb.createFeedbackQuestion(expectedFq);
        verifyPresentInDatabase(expectedFq);

        ______TS("failure: duplicate question, throws error");
        assertThrows(EntityAlreadyExistsException.class, () -> fqDb.createFeedbackQuestion(expectedFq));

        ______TS("failure: invalid question, throws error");
        FeedbackQuestion invalidFq = getTypicalFeedbackQuestionForSession(
                getTypicalFeedbackSessionForCourse(getTypicalCourse()));
        invalidFq.setGiverType(FeedbackParticipantType.RECEIVER);

        assertThrows(InvalidParametersException.class, () -> fqDb.createFeedbackQuestion(invalidFq));
        assertNull(fqDb.getFeedbackQuestion(invalidFq.getId()));

        ______TS("failure: null parameter, assertion error");
        assertThrows(AssertionError.class, () -> fqDb.createFeedbackQuestion(null));
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
        FeedbackQuestion fq7 = typicalDataBundle.feedbackQuestions.get("qn7InSession1InCourse1");
        FeedbackQuestion fq8 = typicalDataBundle.feedbackQuestions.get("qn8InSession1InCourse1");
        FeedbackQuestion fq9 = typicalDataBundle.feedbackQuestions.get("qn9InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4, fq5, fq6, fq7, fq8, fq9);

        List<FeedbackQuestion> actualQuestions = fqDb.getFeedbackQuestionsForSession(fs.getId());

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));

        ______TS("failure: session does not exist, returns no questions");
        actualQuestions = fqDb.getFeedbackQuestionsForSession(UUID.randomUUID());
        assertEquals(0, actualQuestions.size());
    }

    @Test
    public void testGetFeedbackQuestionsForGiverType() {
        ______TS("success: typical case");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq9 = typicalDataBundle.feedbackQuestions.get("qn9InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq9);

        List<FeedbackQuestion> actualQuestions = fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.STUDENTS);

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));

        ______TS("failure: session does not exist, returns no questions");
        fs = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        actualQuestions = fqDb.getFeedbackQuestionsForGiverType(fs, FeedbackParticipantType.STUDENTS);
        assertEquals(0, actualQuestions.size());
    }

    @Test
    public void testDeleteFeedbackQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        verifyPresentInDatabase(fq);

        fqDb.deleteFeedbackQuestion(fq.getId());
        assertNull(fqDb.getFeedbackQuestion(fq.getId()));

        ______TS("failure: null parameter, assertion error");
        assertThrows(AssertionError.class, () -> fqDb.deleteFeedbackQuestion(null));
    }

    @Test
    public void testHasFeedbackQuestionsForGiverType() {
        ______TS("success: typical case");
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        boolean actual = fqDb.hasFeedbackQuestionsForGiverType(
                fs.getName(), course.getId(), FeedbackParticipantType.STUDENTS);

        assertTrue(actual);

        ______TS("failure: session/course does not exist, returns false");
        actual = fqDb.hasFeedbackQuestionsForGiverType("session-name", "course-id", FeedbackParticipantType.STUDENTS);
        assertFalse(actual);
    }
}
