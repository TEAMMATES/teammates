package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityDoesNotExistException;
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
    private FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private FeedbackResponseCommentsDb frcDb;

    @BeforeMethod
    public void setUpMethod() {
        frcDb = mock(FeedbackResponseCommentsDb.class);
        frcLogic.initLogicDependencies(frcDb);
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
