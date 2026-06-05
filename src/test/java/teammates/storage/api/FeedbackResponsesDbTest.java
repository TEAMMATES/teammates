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
        var feedbackResponse = given.feedbackResponse("feedback-response");
        persistGivenData(given);

        FeedbackResponse actual = inTransaction(() -> feedbackResponsesDb.getFeedbackResponse(feedbackResponse.id()));

        assertNotNull(actual);
        assertEquals(feedbackResponse.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistFeedbackResponse_feedbackResponseIsNew_feedbackResponseIsPersisted() {
        var feedbackQuestionRef = given.feedbackQuestion("feedback-question");
        var giverRef = given.student("giver");
        var recipientRef = given.student("recipient");
        persistGivenData(given);
        var feedbackResponseId = given.uuid("feedback-response");

        FeedbackResponse actual = inTransaction(() -> {
            FeedbackQuestion feedbackQuestion = getEntity(FeedbackQuestion.class, feedbackQuestionRef.id());
            Student giver = getEntity(Student.class, giverRef.id());
            Student recipient = getEntity(Student.class, recipientRef.id());
            FeedbackResponse feedbackResponse = buildDefaultFeedbackResponse(
                    feedbackQuestion, giver, recipient, feedbackResponseId);
            return feedbackResponsesDb.persistFeedbackResponse(feedbackResponse);
        });

        assertEquals(feedbackResponseId, actual.getId());
        verifyPresentInDatabase(FeedbackResponse.class, feedbackResponseId);
    }

    @Test(groups = GroupNames.DB)
    public void removeFeedbackResponse_feedbackResponseExists_feedbackResponseIsRemoved() {
        var feedbackResponse = given.feedbackResponse("feedback-response");
        persistGivenData(given);

        inTransaction(() -> feedbackResponsesDb.removeFeedbackResponse(
                feedbackResponsesDb.getFeedbackResponse(feedbackResponse.id())));

        verifyAbsentInDatabase(FeedbackResponse.class, feedbackResponse.id());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackResponsesFromGiverForQuestion_responsesExist_returnsResponsesFromGiver() {
        var feedbackQuestion = given.feedbackQuestion("feedback-question");
        var giver = given.student("giver");
        var feedbackResponse = given.feedbackResponse("feedback-response",
                r -> r.feedbackQuestion(feedbackQuestion.alias()).giverStudent(giver.alias()));
        given.feedbackResponse("another-giver-feedback-response",
                r -> r.feedbackQuestion(feedbackQuestion.alias()).giverStudent("another-giver"));
        persistGivenData(given);

        List<FeedbackResponse> actual = inTransaction(() -> feedbackResponsesDb.getFeedbackResponsesFromGiverForQuestion(
                feedbackQuestion.id(), giver.id(), null));

        assertEquals(List.of(feedbackResponse.id()), actual.stream().map(FeedbackResponse::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackResponsesForRecipientForQuestion_responsesExist_returnsResponsesForRecipient() {
        var feedbackQuestion = given.feedbackQuestion("feedback-question");
        var recipient = given.student("recipient");
        var feedbackResponse = given.feedbackResponse("feedback-response",
                r -> r.feedbackQuestion(feedbackQuestion.alias()).recipientStudent(recipient.alias()));
        given.feedbackResponse("another-recipient-feedback-response",
                r -> r.feedbackQuestion(feedbackQuestion.alias()).recipientStudent("another-recipient"));
        persistGivenData(given);

        List<FeedbackResponse> actual = inTransaction(() -> feedbackResponsesDb
                .getFeedbackResponsesForRecipientForQuestion(feedbackQuestion.id(), recipient.id(), null));

        assertEquals(List.of(feedbackResponse.id()), actual.stream().map(FeedbackResponse::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getFeedbackResponsesForSession_responsesExist_returnsResponsesInSessionAndCourse() {
        var course = given.course("course");
        var feedbackSession = given.feedbackSession("feedback-session", fs -> fs.course(course.alias()));
        var feedbackResponse = given.feedbackResponse("feedback-response",
                r -> r.feedbackSession(feedbackSession.alias()));
        given.feedbackResponse("another-session-feedback-response",
                r -> r.feedbackSession("another-feedback-session"));
        persistGivenData(given);

        List<FeedbackResponse> actual = inTransaction(() -> feedbackResponsesDb.getFeedbackResponsesForSession(
                getEntity(FeedbackSession.class, feedbackSession.id()), course.id()));

        assertEquals(Set.of(feedbackResponse.id()),
                actual.stream().map(FeedbackResponse::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void hasResponsesForCourse_responseExists_returnsTrue() {
        var course = given.course("course");
        given.feedbackResponse("feedback-response", r -> r.course(course.alias()));
        persistGivenData(given);

        boolean actual = inTransaction(() -> feedbackResponsesDb.hasResponsesForCourse(course.id()));

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
