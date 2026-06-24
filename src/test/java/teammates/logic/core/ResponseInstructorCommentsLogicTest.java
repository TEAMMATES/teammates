package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.ResponseInstructorCommentsDb;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.test.BaseTestCase;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * SUT: {@link ResponseInstructorCommentsLogic}.
 */
public class ResponseInstructorCommentsLogicTest extends BaseTestCase {

    private static final UUID TYPICAL_ID = UUID.fromString("00000000-0000-4000-8000-000000000064");
    private static final UUID NOT_TYPICAL_ID = UUID.fromString("00000000-0000-4000-8000-000000000065");
    private ResponseInstructorCommentsLogic frcLogic = ResponseInstructorCommentsLogic.inst();
    private ResponseInstructorCommentsDb frcDb;
    private FeedbackResponsesLogic frLogic;

    @BeforeMethod
    public void setUpMethod() {
        frcDb = mock(ResponseInstructorCommentsDb.class);
        frLogic = mock(FeedbackResponsesLogic.class);
        frcLogic.initLogicDependencies(frcDb, frLogic);
    }

    @Test
    public void testGetComment_commentAlreadyExists_success() {
        ResponseInstructorComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getResponseInstructorComment(comment.getId())).thenReturn(comment);

        ResponseInstructorComment commentFetched = frcLogic.getResponseInstructorComment(TYPICAL_ID);

        assertEquals(comment, commentFetched);
    }

    @Test
    public void testGetComment_commentDoesNotExist_returnsNull() {
        when(frcDb.getResponseInstructorComment(NOT_TYPICAL_ID)).thenReturn(null);

        ResponseInstructorComment commentFetched = frcLogic.getResponseInstructorComment(NOT_TYPICAL_ID);

        verify(frcDb, times(1)).getResponseInstructorComment(NOT_TYPICAL_ID);
        assertNull(commentFetched);
    }

    @Test
    public void testCreateComment_commentDoesNotExist_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        ResponseInstructorComment comment = getTypicalResponseComment(TYPICAL_ID);
        FeedbackResponse feedbackResponse = comment.getFeedbackResponse();
        Instructor giver = getRandomInstructor();

        when(frLogic.getFeedbackResponse(feedbackResponse.getId())).thenReturn(feedbackResponse);
        when(frcDb.persistResponseInstructorComment(any(ResponseInstructorComment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseInstructorComment createdComment = frcLogic.createResponseInstructorComment(
                feedbackResponse.getId(), giver, "new comment");

        verify(frcDb, times(1)).persistResponseInstructorComment(createdComment);
        assertEquals("new comment", createdComment.getCommentText());
        assertEquals(giver, createdComment.getGiver());
    }

    @Test
    public void testDeleteComment_commentExists_success() {
        ResponseInstructorComment comment = getTypicalResponseComment(TYPICAL_ID);
        when(frcDb.getResponseInstructorComment(TYPICAL_ID)).thenReturn(comment);

        frcLogic.deleteResponseInstructorComment(TYPICAL_ID);

        verify(frcDb, times(1)).removeResponseInstructorComment(comment);
    }

    @Test
    public void testUpdateComment_entityAlreadyExists_success()
            throws EntityDoesNotExistException {
        ResponseInstructorComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getResponseInstructorComment(comment.getId())).thenReturn(comment);

        String updatedCommentText = "Update";
        ResponseInstructorCommentUpdateRequest updateRequest = new ResponseInstructorCommentUpdateRequest(
                updatedCommentText);
        ResponseInstructorComment updatedComment = frcLogic.updateResponseInstructorComment(TYPICAL_ID, updateRequest,
                getRandomInstructor());

        verify(frcDb, times(1)).getResponseInstructorComment(TYPICAL_ID);

        assertEquals(TYPICAL_ID, updatedComment.getId());
        assertEquals(updatedCommentText, updatedComment.getCommentText());
    }

    @Test
    public void testUpdateComment_entityDoesNotExist() {
        ResponseInstructorComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getResponseInstructorComment(comment.getId())).thenReturn(comment);

        UUID nonExistentId = UUID.fromString("00000000-0000-4000-8000-000000009999");
        ResponseInstructorCommentUpdateRequest updateRequest = new ResponseInstructorCommentUpdateRequest("Update");

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.updateResponseInstructorComment(nonExistentId, updateRequest,
                        getRandomInstructor()
                ));

        assertEquals("Trying to update a feedback response comment that does not exist.", ex.getMessage());
    }

    private ResponseInstructorComment getTypicalResponseComment(UUID id) {
        ResponseInstructorComment comment = getTypicalResponseInstructorComment();
        comment.setId(id);
        return comment;
    }

    private Instructor getRandomInstructor() {
        Instructor instructor = getTypicalInstructor();
        instructor.setId(UUID.randomUUID());
        return instructor;
    }
}
