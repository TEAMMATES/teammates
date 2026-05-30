package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.test.BaseTestCase;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;

/**
 * SUT: {@link FeedbackResponseCommentsLogic}.
 */
public class FeedbackResponseCommentsLogicTest extends BaseTestCase {

    private static final UUID TYPICAL_ID = UUID.fromString("00000000-0000-4000-8000-000000000064");
    private static final UUID NOT_TYPICAL_ID = UUID.fromString("00000000-0000-4000-8000-000000000065");
    private FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private FeedbackResponseCommentsDb frcDb;
    private FeedbackResponsesLogic frLogic;

    @BeforeMethod
    public void setUpMethod() {
        frcDb = mock(FeedbackResponseCommentsDb.class);
        frLogic = mock(FeedbackResponsesLogic.class);
        frcLogic.initLogicDependencies(frcDb, frLogic);
    }

    @Test
    public void testGetComment_commentAlreadyExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getFeedbackResponseComment(comment.getId())).thenReturn(comment);

        FeedbackResponseComment commentFetched = frcLogic.getFeedbackResponseComment(TYPICAL_ID);

        assertEquals(comment, commentFetched);
    }

    @Test
    public void testGetComment_commentDoesNotExist_returnsNull() {
        when(frcDb.getFeedbackResponseComment(NOT_TYPICAL_ID)).thenReturn(null);

        FeedbackResponseComment commentFetched = frcLogic.getFeedbackResponseComment(NOT_TYPICAL_ID);

        verify(frcDb, times(1)).getFeedbackResponseComment(NOT_TYPICAL_ID);
        assertNull(commentFetched);
    }

    @Test
    public void testCreateComment_commentDoesNotExist_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);
        FeedbackResponse feedbackResponse = comment.getFeedbackResponse();
        ResponseGiver giver = getRandomInstructorGiver();

        when(frLogic.getFeedbackResponse(feedbackResponse.getId())).thenReturn(feedbackResponse);
        when(frcDb.createFeedbackResponseComment(any(FeedbackResponseComment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FeedbackResponseComment createdComment = frcLogic.createFeedbackResponseComment(feedbackResponse.getId(), giver,
                "new comment", List.of(ViewerType.STUDENTS), List.of(ViewerType.INSTRUCTORS));

        verify(frcDb, times(1)).createFeedbackResponseComment(createdComment);
        assertEquals("new comment", createdComment.getCommentText());
        assertEquals(giver, createdComment.getGiver());
        assertEquals(List.of(ViewerType.STUDENTS), createdComment.getShowCommentTo());
        assertEquals(List.of(ViewerType.INSTRUCTORS), createdComment.getShowGiverNameTo());
    }

    @Test
    public void testDeleteComment_commentExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);
        when(frcDb.getFeedbackResponseComment(TYPICAL_ID)).thenReturn(comment);

        frcLogic.deleteFeedbackResponseComment(TYPICAL_ID);

        verify(frcDb, times(1)).deleteFeedbackResponseComment(comment);
    }

    @Test
    public void testUpdateComment_entityAlreadyExists_success()
            throws EntityDoesNotExistException {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getFeedbackResponseComment(comment.getId())).thenReturn(comment);

        String updatedCommentText = "Update";
        List<CommentVisibilityType> showCommentTo = new ArrayList<>();
        showCommentTo.add(CommentVisibilityType.STUDENTS);
        showCommentTo.add(CommentVisibilityType.INSTRUCTORS);
        List<CommentVisibilityType> showGiverNameTo = new ArrayList<>();
        showGiverNameTo.add(CommentVisibilityType.INSTRUCTORS);

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                updatedCommentText, showCommentTo, showGiverNameTo);
        FeedbackResponseComment updatedComment = frcLogic.updateFeedbackResponseComment(TYPICAL_ID, updateRequest,
                getRandomInstructorGiver());

        verify(frcDb, times(1)).getFeedbackResponseComment(TYPICAL_ID);

        List<ViewerType> expectedShowCommentTo = new ArrayList<>();
        expectedShowCommentTo.add(ViewerType.STUDENTS);
        expectedShowCommentTo.add(ViewerType.INSTRUCTORS);
        List<ViewerType> expectedShowGiverNameTo = new ArrayList<>();
        expectedShowGiverNameTo.add(ViewerType.INSTRUCTORS);

        assertEquals(TYPICAL_ID, updatedComment.getId());
        assertEquals(updatedCommentText, updatedComment.getCommentText());
        assertEquals(expectedShowCommentTo, updatedComment.getShowCommentTo());
        assertEquals(expectedShowGiverNameTo, updatedComment.getShowGiverNameTo());
    }

    @Test
    public void testUpdateComment_entityDoesNotExist() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getFeedbackResponseComment(comment.getId())).thenReturn(comment);

        UUID nonExistentId = UUID.fromString("00000000-0000-4000-8000-000000009999");
        String updatedCommentText = "Update";
        List<CommentVisibilityType> showCommentTo = new ArrayList<>();
        showCommentTo.add(CommentVisibilityType.STUDENTS);
        showCommentTo.add(CommentVisibilityType.INSTRUCTORS);
        List<CommentVisibilityType> showGiverNameTo = new ArrayList<>();
        showGiverNameTo.add(CommentVisibilityType.INSTRUCTORS);

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                updatedCommentText, showCommentTo, showGiverNameTo);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.updateFeedbackResponseComment(nonExistentId, updateRequest,
                        getRandomInstructorGiver()
                ));

        assertEquals("Trying to update a feedback response comment that does not exist.", ex.getMessage());
    }

    private FeedbackResponseComment getTypicalResponseComment(UUID id) {
        FeedbackResponseComment comment = getTypicalFeedbackResponseComment();
        comment.setId(id);
        return comment;
    }

    private ResponseGiver getRandomInstructorGiver() {
        Instructor instructor = getTypicalInstructor();
        instructor.setId(UUID.randomUUID());
        return new ResponseGiver(instructor);
    }
}
