package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.test.BaseTestCaseWithDatabaseAccess;

/**
 * SUT: {@link FeedbackQuestionsDb}.
 */
public class FeedbackQuestionsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final FeedbackSessionsDb fsDb = FeedbackSessionsDb.inst();
    private final FeedbackQuestionsDb fqDb = FeedbackQuestionsDb.inst();

    private DataBundle typicalDataBundle;

    @BeforeMethod
    protected void setUp() {
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test
    public void testGetFeedbackQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion expectedFq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        FeedbackQuestion actualFq = inTransaction(() -> fqDb.getFeedbackQuestion(expectedFq.getId()));

        assertEquals(expectedFq, actualFq);

        ______TS("failure: does not exist, returns null");
        actualFq = inTransaction(() -> fqDb.getFeedbackQuestion(UUID.randomUUID()));
        assertNull(actualFq);
    }

    @Test
    public void testPersistFeedbackQuestion() {
        Course course = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(course);

        FeedbackQuestion expectedFq = inTransaction(() -> {
            coursesDb.persistCourse(course);
            fsDb.persistFeedbackSession(fs);
            FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);
            fqDb.persistFeedbackQuestion(fq);
            return fq;
        });
        verifyPresentInDatabase(expectedFq);
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

        List<FeedbackQuestion> actualQuestions = inTransaction(() -> fqDb.getFeedbackQuestionsForSession(fs.getId()));

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));

        ______TS("failure: session does not exist, returns no questions");
        actualQuestions = inTransaction(() -> fqDb.getFeedbackQuestionsForSession(UUID.randomUUID()));
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

        List<FeedbackQuestion> actualQuestions =
                inTransaction(() -> fqDb.getFeedbackQuestionsForGiverType(fs, QuestionGiverType.STUDENTS));

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));

        ______TS("failure: session does not exist, returns no questions");
        FeedbackSession nonExistentFs = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        actualQuestions = inTransaction(() -> fqDb.getFeedbackQuestionsForGiverType(
                nonExistentFs, QuestionGiverType.STUDENTS));
        assertEquals(0, actualQuestions.size());
    }

    @Test
    public void testRemoveFeedbackQuestion() {
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        verifyPresentInDatabase(fq);

        inTransaction(() -> fqDb.removeFeedbackQuestion(fq));
        assertNull(inTransaction(() -> fqDb.getFeedbackQuestion(fq.getId())));
    }

    @Test
    public void testHasFeedbackQuestionsForGiverType() {
        ______TS("success: typical case");
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        boolean actual = inTransaction(() -> fqDb.hasFeedbackQuestionsForGiverType(
                fs.getName(), course.getId(), QuestionGiverType.STUDENTS));

        assertTrue(actual);

        ______TS("failure: session/course does not exist, returns false");
        actual = inTransaction(() -> fqDb.hasFeedbackQuestionsForGiverType(
                "session-name", "course-id", QuestionGiverType.STUDENTS));
        assertFalse(actual);
    }
}
