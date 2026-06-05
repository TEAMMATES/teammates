package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;

/**
 * Tests for {@link FeedbackQuestionsDb}.
 */
public class FeedbackQuestionsDbTest extends BaseDbTestcase {
    private final FeedbackQuestionsDb feedbackQuestionsDb = FeedbackQuestionsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getFeedbackQuestion_feedbackQuestionExists_returnsFeedbackQuestion() {
        var feedbackQuestion = given.feedbackQuestion("feedback-question");
        persistGivenData(given);

        FeedbackQuestion actual = inTransaction(() -> feedbackQuestionsDb.getFeedbackQuestion(feedbackQuestion.id()));

        assertNotNull(actual);
        assertEquals(feedbackQuestion.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistFeedbackQuestion_feedbackQuestionIsNew_feedbackQuestionIsPersisted() {
        var feedbackSessionRef = given.feedbackSession("feedback-session");
        persistGivenData(given);
        var feedbackQuestionId = given.uuid("feedback-question");

        FeedbackQuestion actual = inTransaction(() -> {
            FeedbackSession feedbackSession = getEntity(FeedbackSession.class, feedbackSessionRef.id());
            FeedbackQuestion feedbackQuestion = buildDefaultFeedbackQuestion(feedbackSession, feedbackQuestionId);
            return feedbackQuestionsDb.persistFeedbackQuestion(feedbackQuestion);
        });

        assertEquals(feedbackQuestionId, actual.getId());
        verifyPresentInDatabase(FeedbackQuestion.class, feedbackQuestionId);
    }

    @Test(groups = GroupNames.DB)
    public void removeFeedbackQuestion_feedbackQuestionExists_feedbackQuestionIsRemoved() {
        var feedbackQuestion = given.feedbackQuestion("feedback-question");
        persistGivenData(given);

        inTransaction(() -> feedbackQuestionsDb.removeFeedbackQuestion(
                feedbackQuestionsDb.getFeedbackQuestion(feedbackQuestion.id())));

        verifyAbsentInDatabase(FeedbackQuestion.class, feedbackQuestion.id());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackQuestionsForSession_questionsExist_returnsQuestionsInSession() {
        var feedbackSession = given.feedbackSession("feedback-session");
        var feedbackQuestion1 = given.feedbackQuestion("feedback-question-1",
                q -> q.feedbackSession(feedbackSession.alias()));
        var feedbackQuestion2 = given.feedbackQuestion("feedback-question-2",
                q -> q.feedbackSession(feedbackSession.alias()));
        given.feedbackQuestion("another-session-feedback-question",
                q -> q.feedbackSession("another-feedback-session"));
        persistGivenData(given);

        List<FeedbackQuestion> actual = inTransaction(() -> feedbackQuestionsDb.getFeedbackQuestionsForSession(
                feedbackSession.id()));

        assertEquals(Set.of(feedbackQuestion1.id(), feedbackQuestion2.id()),
                actual.stream().map(FeedbackQuestion::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackQuestionsForGiverType_questionsExist_returnsQuestionsForGiverType() {
        var feedbackSession = given.feedbackSession("feedback-session");
        var feedbackQuestion = given.feedbackQuestion("student-feedback-question",
                q -> q.feedbackSession(feedbackSession.alias()).giverType(QuestionGiverType.STUDENTS));
        given.feedbackQuestion("instructor-feedback-question",
                q -> q.feedbackSession(feedbackSession.alias()).giverType(QuestionGiverType.INSTRUCTORS));
        persistGivenData(given);

        List<FeedbackQuestion> actual = inTransaction(() -> {
            FeedbackSession feedbackSessionEntity = getEntity(FeedbackSession.class, feedbackSession.id());
            return feedbackQuestionsDb.getFeedbackQuestionsForGiverType(
                    feedbackSessionEntity, QuestionGiverType.STUDENTS);
        });

        assertEquals(List.of(feedbackQuestion.id()), actual.stream().map(FeedbackQuestion::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void hasFeedbackQuestionsForGiverType_matchingQuestionExists_returnsTrue() {
        var course = given.course("course");
        var feedbackSession = given.feedbackSession("feedback-session",
                fs -> fs.course(course.alias()).name("Feedback Session"));
        given.feedbackQuestion("feedback-question",
                q -> q.feedbackSession(feedbackSession.alias()).giverType(QuestionGiverType.STUDENTS));
        persistGivenData(given);

        boolean actual = inTransaction(() -> feedbackQuestionsDb.hasFeedbackQuestionsForGiverType(
                "Feedback Session", course.id(), QuestionGiverType.STUDENTS));

        assertTrue(actual);
    }

    private static FeedbackQuestion buildDefaultFeedbackQuestion(
            FeedbackSession feedbackSession, UUID feedbackQuestionId) {
        assertNotNull(feedbackSession);
        FeedbackQuestion feedbackQuestion = FeedbackQuestion.makeQuestion(
                1,
                "Question Description",
                QuestionGiverType.SESSION_CREATOR,
                QuestionRecipientType.SELF,
                Const.MAX_POSSIBLE_RECIPIENTS,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new FeedbackTextQuestionDetails("Question Text"));
        feedbackQuestion.setId(feedbackQuestionId);
        feedbackSession.addFeedbackQuestion(feedbackQuestion);
        return feedbackQuestion;
    }
}
