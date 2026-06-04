package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;

/**
 * Tests for {@link FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbTest extends BaseDbTestcase {
    private final FeedbackResponsesDb feedbackResponsesDb = FeedbackResponsesDb.inst();

    @Test(groups = GroupNames.DB)
    public void getFeedbackResponse_feedbackResponseExists_returnsFeedbackResponse() {
        UUID feedbackResponseId = given.feedbackResponse("feedback-response");
        persistGivenData(given);

        FeedbackResponse actual = inTransaction(() -> feedbackResponsesDb.getFeedbackResponse(feedbackResponseId));

        assertNotNull(actual);
        assertEquals(feedbackResponseId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistFeedbackResponse_feedbackResponseIsNew_feedbackResponseIsPersisted() {
        UUID feedbackQuestionId = given.feedbackQuestion("feedback-question");
        UUID giverId = given.student("giver");
        UUID recipientId = given.student("recipient");
        persistGivenData(given);
        UUID feedbackResponseId = given.uuid("feedback-response");

        FeedbackResponse actual = inTransaction(() -> {
            FeedbackQuestion feedbackQuestion = getEntity(FeedbackQuestion.class, feedbackQuestionId);
            Student giver = getEntity(Student.class, giverId);
            Student recipient = getEntity(Student.class, recipientId);
            FeedbackResponse feedbackResponse = buildDefaultFeedbackResponse(
                    feedbackQuestion, giver, recipient, feedbackResponseId);
            return feedbackResponsesDb.persistFeedbackResponse(feedbackResponse);
        });

        assertEquals(feedbackResponseId, actual.getId());
        verifyPresentInDatabase(FeedbackResponse.class, feedbackResponseId);
    }

    @Test(groups = GroupNames.DB)
    public void removeFeedbackResponse_feedbackResponseExists_feedbackResponseIsRemoved() {
        UUID feedbackResponseId = given.feedbackResponse("feedback-response");
        persistGivenData(given);

        inTransaction(() -> feedbackResponsesDb.removeFeedbackResponse(
                feedbackResponsesDb.getFeedbackResponse(feedbackResponseId)));

        verifyAbsentInDatabase(FeedbackResponse.class, feedbackResponseId);
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackResponsesFromGiverForQuestion_responsesExist_returnsResponsesFromGiver() {
        UUID feedbackQuestionId = given.feedbackQuestion("feedback-question");
        UUID giverId = given.student("giver");
        UUID feedbackResponseId = given.feedbackResponse("feedback-response",
                r -> r.feedbackQuestion("feedback-question").giverStudent("giver"));
        given.feedbackResponse("another-giver-feedback-response",
                r -> r.feedbackQuestion("feedback-question").giverStudent("another-giver"));
        persistGivenData(given);

        List<FeedbackResponse> actual = inTransaction(() -> feedbackResponsesDb.getFeedbackResponsesFromGiverForQuestion(
                feedbackQuestionId, giverId, null));

        assertEquals(List.of(feedbackResponseId), actual.stream().map(FeedbackResponse::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackResponsesForRecipientForQuestion_responsesExist_returnsResponsesForRecipient() {
        UUID feedbackQuestionId = given.feedbackQuestion("feedback-question");
        UUID recipientId = given.student("recipient");
        UUID feedbackResponseId = given.feedbackResponse("feedback-response",
                r -> r.feedbackQuestion("feedback-question").recipientStudent("recipient"));
        given.feedbackResponse("another-recipient-feedback-response",
                r -> r.feedbackQuestion("feedback-question").recipientStudent("another-recipient"));
        persistGivenData(given);

        List<FeedbackResponse> actual = inTransaction(() -> feedbackResponsesDb
                .getFeedbackResponsesForRecipientForQuestion(feedbackQuestionId, recipientId, null));

        assertEquals(List.of(feedbackResponseId), actual.stream().map(FeedbackResponse::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackResponsesForSession_responsesExist_returnsResponsesInSessionAndCourse() {
        String courseId = given.course("course");
        UUID feedbackSessionId = given.feedbackSession("feedback-session", fs -> fs.course("course"));
        UUID feedbackResponseId = given.feedbackResponse("feedback-response",
                r -> r.feedbackSession("feedback-session"));
        given.feedbackResponse("another-session-feedback-response",
                r -> r.feedbackSession("another-feedback-session"));
        persistGivenData(given);

        List<FeedbackResponse> actual = inTransaction(() -> feedbackResponsesDb.getFeedbackResponsesForSession(
                getEntity(FeedbackSession.class, feedbackSessionId), courseId));

        assertEquals(Set.of(feedbackResponseId),
                actual.stream().map(FeedbackResponse::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void hasResponsesForCourse_responseExists_returnsTrue() {
        String courseId = given.course("course");
        given.feedbackResponse("feedback-response", r -> r.course("course"));
        persistGivenData(given);

        boolean actual = inTransaction(() -> feedbackResponsesDb.hasResponsesForCourse(courseId));

        assertTrue(actual);
    }

    private static FeedbackResponse buildDefaultFeedbackResponse(
            FeedbackQuestion feedbackQuestion, Student giver, Student recipient, UUID feedbackResponseId) {
        assertNotNull(feedbackQuestion);
        assertNotNull(giver);
        assertNotNull(recipient);
        FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                new ResponseGiver(giver), new ResponseRecipient(recipient),
                new FeedbackTextResponseDetails("Response"));
        feedbackResponse.setId(feedbackResponseId);
        feedbackQuestion.addFeedbackResponse(feedbackResponse);
        return feedbackResponse;
    }
}
