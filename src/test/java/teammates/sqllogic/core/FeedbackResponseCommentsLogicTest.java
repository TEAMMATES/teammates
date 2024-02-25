package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.test.BaseTestCase;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;

/**
 * SUT: {@link FeedbackResponseCommentsLogic}.
 */
public class FeedbackResponseCommentsLogicTest extends BaseTestCase {

    private static final Long TYPICAL_ID = 100L;
    private static final Long NOT_TYPICAL_ID = 101L;
    private static final UUID TYPICAL_UUID = UUID.randomUUID();
    private FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private FeedbackResponseCommentsDb frcDb;

    @BeforeMethod
    public void setUpMethod() {
        frcDb = mock(FeedbackResponseCommentsDb.class);
        frcLogic.initLogicDependencies(frcDb);
    }

    @Test
    public void testGetComment_commentAlreadyExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getFeedbackResponseComment(comment.getId())).thenReturn(comment);

        FeedbackResponseComment commentFetched = frcLogic.getFeedbackResponseComment(TYPICAL_ID);

        assertEquals(comment, commentFetched);
    }

    @Test
    public void testGetCommentForResponse_commentAlreadyExists_success() {
        List<FeedbackResponseComment> expectedReturn = new ArrayList<>();
        expectedReturn.add(getTypicalResponseComment(TYPICAL_ID));

        when(frcDb.getFeedbackResponseCommentsForResponse(TYPICAL_UUID)).thenReturn(expectedReturn);

        List<FeedbackResponseComment> fetchedReturn = frcLogic.getFeedbackResponseCommentsForResponse(TYPICAL_UUID);

        assertEquals(expectedReturn, fetchedReturn);
    }

    @Test
    public void testGetCommentForResponseFromParticipant_commentAlreadyExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getFeedbackResponseCommentForResponseFromParticipant(TYPICAL_UUID)).thenReturn(comment);

        FeedbackResponseComment commentFetched = frcLogic
                .getFeedbackResponseCommentForResponseFromParticipant(TYPICAL_UUID);

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
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        frcLogic.createFeedbackResponseComment(comment);

        verify(frcDb, times(1)).createFeedbackResponseComment(comment);
    }

    @Test
    public void testCreateComment_commentAlreadyExists_throwsEntityAlreadyExistsException()
            throws EntityAlreadyExistsException, InvalidParametersException {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.createFeedbackResponseComment(comment)).thenThrow(EntityAlreadyExistsException.class);

        assertThrows(EntityAlreadyExistsException.class,
                () -> frcLogic.createFeedbackResponseComment(comment));

    }

    @Test
    public void testDeleteComment_commentExists_success() {
        frcLogic.deleteFeedbackResponseComment(TYPICAL_ID);

        verify(frcDb, times(1)).deleteFeedbackResponseComment(TYPICAL_ID);
    }

    @Test
    public void testUpdateCommentEmails_success() {
        String courseId = "Course_id";
        String oldEmail = "oldEmail@gmail.com";
        String newEmail = "newEmail@gmail.com";
        frcLogic.updateFeedbackResponseCommentsEmails(courseId, oldEmail, newEmail);

        verify(frcDb, times(1)).updateGiverEmailOfFeedbackResponseComments(courseId, oldEmail, newEmail);
        verify(frcDb, times(1)).updateLastEditorEmailOfFeedbackResponseComments(courseId, oldEmail, newEmail);
    }

    @Test
    public void testUpdateComment_entityAlreadyExists_success()
            throws EntityDoesNotExistException {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getFeedbackResponseComment(comment.getId())).thenReturn(comment);

        String updatedCommentText = "Update";
        String lastEditorEmail = "me@gmail.com";
        List<CommentVisibilityType> showCommentTo = new ArrayList<>();
        showCommentTo.add(CommentVisibilityType.STUDENTS);
        showCommentTo.add(CommentVisibilityType.INSTRUCTORS);
        List<CommentVisibilityType> showGiverNameTo = new ArrayList<>();
        showGiverNameTo.add(CommentVisibilityType.INSTRUCTORS);

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                updatedCommentText, showCommentTo, showGiverNameTo);
        FeedbackResponseComment updatedComment = frcLogic.updateFeedbackResponseComment(TYPICAL_ID, updateRequest,
                lastEditorEmail);

        verify(frcDb, times(1)).getFeedbackResponseComment(TYPICAL_ID);

        List<FeedbackParticipantType> expectedShowCommentTo = new ArrayList<>();
        expectedShowCommentTo.add(FeedbackParticipantType.STUDENTS);
        expectedShowCommentTo.add(FeedbackParticipantType.INSTRUCTORS);
        List<FeedbackParticipantType> expectedShowGiverNameTo = new ArrayList<>();
        expectedShowGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);

        assertEquals(TYPICAL_ID, updatedComment.getId());
        assertEquals(updatedCommentText, updatedComment.getCommentText());
        assertEquals(expectedShowCommentTo, updatedComment.getShowCommentTo());
        assertEquals(expectedShowGiverNameTo, updatedComment.getShowGiverNameTo());
        assertEquals(lastEditorEmail, updatedComment.getLastEditorEmail());
    }

    @Test
    public void testUpdateComment_entityDoesNotExist() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        when(frcDb.getFeedbackResponseComment(comment.getId())).thenReturn(comment);

        long nonExistentId = 101L;
        String updatedCommentText = "Update";
        String lastEditorEmail = "me@gmail.com";
        List<CommentVisibilityType> showCommentTo = new ArrayList<>();
        showCommentTo.add(CommentVisibilityType.STUDENTS);
        showCommentTo.add(CommentVisibilityType.INSTRUCTORS);
        List<CommentVisibilityType> showGiverNameTo = new ArrayList<>();
        showGiverNameTo.add(CommentVisibilityType.INSTRUCTORS);

        FeedbackResponseCommentUpdateRequest updateRequest = new FeedbackResponseCommentUpdateRequest(
                updatedCommentText, showCommentTo, showGiverNameTo);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.updateFeedbackResponseComment(nonExistentId, updateRequest, lastEditorEmail));

        assertEquals("Trying to update a feedback response comment that does not exist.", ex.getMessage());
    }
}
