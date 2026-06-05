package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.test.GroupNames;

/**
 * Tests for {@link ResponseInstructorCommentsDb}.
 */
public class ResponseInstructorCommentsDbTest extends BaseDbTestcase {
    private final ResponseInstructorCommentsDb responseInstructorCommentsDb = ResponseInstructorCommentsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getResponseInstructorComment_responseInstructorCommentExists_returnsResponseInstructorComment() {
        var responseInstructorComment = given.responseInstructorComment("response-instructor-comment");
        persistGivenData(given);

        ResponseInstructorComment actual = inTransaction(() -> responseInstructorCommentsDb
                .getResponseInstructorComment(responseInstructorComment.id()));

        assertNotNull(actual);
        assertEquals(responseInstructorComment.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistResponseInstructorComment_responseInstructorCommentIsNew_commentIsPersisted() {
        var feedbackResponseRef = given.feedbackResponse("feedback-response");
        var instructorRef = given.instructor("instructor");
        persistGivenData(given);
        var responseInstructorCommentId = given.uuid("response-instructor-comment");

        ResponseInstructorComment actual = inTransaction(() -> {
            FeedbackResponse feedbackResponse = getEntity(FeedbackResponse.class, feedbackResponseRef.id());
            Instructor instructor = getEntity(Instructor.class, instructorRef.id());
            ResponseInstructorComment comment = buildDefaultResponseInstructorComment(
                    feedbackResponse, instructor, responseInstructorCommentId);
            return responseInstructorCommentsDb.persistResponseInstructorComment(comment);
        });

        assertEquals(responseInstructorCommentId, actual.getId());
        verifyPresentInDatabase(ResponseInstructorComment.class, responseInstructorCommentId);
    }

    @Test(groups = GroupNames.DB)
    public void removeResponseInstructorComment_responseInstructorCommentExists_commentIsRemoved() {
        var responseInstructorComment = given.responseInstructorComment("response-instructor-comment");
        persistGivenData(given);

        inTransaction(() -> responseInstructorCommentsDb.removeResponseInstructorComment(
                responseInstructorCommentsDb.getResponseInstructorComment(responseInstructorComment.id())));

        verifyAbsentInDatabase(ResponseInstructorComment.class, responseInstructorComment.id());
    }

    @Test(groups = GroupNames.DB)
    public void getResponseInstructorCommentsForResponses_commentsExist_returnsCommentsForResponses() {
        var feedbackResponse = given.feedbackResponse("feedback-response");
        var responseInstructorComment = given.responseInstructorComment("response-instructor-comment",
                c -> c.feedbackResponse(feedbackResponse.alias()));
        given.responseInstructorComment("another-response-comment",
                c -> c.feedbackResponse("another-feedback-response"));
        persistGivenData(given);

        List<ResponseInstructorComment> actual = inTransaction(() -> responseInstructorCommentsDb
                .getResponseInstructorCommentsForResponses(List.of(feedbackResponse.id())));

        assertEquals(List.of(responseInstructorComment.id()),
                actual.stream().map(ResponseInstructorComment::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getResponseInstructorCommentsForResponses_responseIdsEmpty_returnsEmptyList() {
        given.responseInstructorComment("response-instructor-comment");
        persistGivenData(given);

        List<ResponseInstructorComment> actual = inTransaction(() -> responseInstructorCommentsDb
                .getResponseInstructorCommentsForResponses(List.of()));

        assertEquals(List.of(), actual);
    }

    private static ResponseInstructorComment buildDefaultResponseInstructorComment(
            FeedbackResponse feedbackResponse, Instructor instructor, UUID responseInstructorCommentId) {
        assertNotNull(feedbackResponse);
        assertNotNull(instructor);
        ResponseInstructorComment comment = new ResponseInstructorComment(
                instructor,
                "Comment",
                List.of(ViewerType.GIVER, ViewerType.RECEIVER, ViewerType.INSTRUCTORS),
                List.of(ViewerType.GIVER, ViewerType.RECEIVER, ViewerType.INSTRUCTORS),
                instructor);
        comment.setId(responseInstructorCommentId);
        feedbackResponse.addResponseInstructorComment(comment);
        return comment;
    }
}
