package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code FeedbackResponseCommentsDb}.
 */

public class FeedbackResponseCommentsDbTest extends BaseTestCase {

    private static final Long TYPICAL_ID = 100L;
    private FeedbackResponseCommentsDb feedbackResponseCommentsDb;
    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        feedbackResponseCommentsDb = spy(FeedbackResponseCommentsDb.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();

    }

    @Test
    public void testCreateComment_commentDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponseComment comment = getTypicalResponseComment();

        feedbackResponseCommentsDb.createFeedbackResponseComment(comment);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(comment));
    }

    @Test
    public void testCreateComment_commentAlreadyExists_throwsEntityAlreadyExistsException() {
        FeedbackResponseComment comment = getTypicalResponseComment();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, TYPICAL_ID)).thenReturn(comment);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> feedbackResponseCommentsDb.createFeedbackResponseComment(comment));

        assertEquals("Trying to create an entity that exists: " + comment.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(comment), never());
    }

    @Test
    public void testGetComment_commentAlreadyExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, TYPICAL_ID)).thenReturn(comment);

        FeedbackResponseComment commentFetched = feedbackResponseCommentsDb.getFeedbackResponseComment(TYPICAL_ID);

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, TYPICAL_ID)).thenReturn(comment);
        assertEquals(comment, commentFetched);
    }

    @Test
    public void testGetComment_commentDoesNotExist_returnsNull() {
        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, 101L)).thenReturn(null);

        FeedbackResponseComment commentFetched = feedbackResponseCommentsDb.getFeedbackResponseComment(101L);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackResponseComment.class, 101L), times(1));
        assertNull(commentFetched);
    }

    @Test
    public void testDeleteComment_commentExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment();

        feedbackResponseCommentsDb.delete(comment);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(comment));
    }

    @Test
    public void testUpdateComment_commentInvalid_throwsInvalidParametersException() {
        FeedbackResponseComment comment = getTypicalResponseComment();
        comment.setGiverType(FeedbackParticipantType.SELF);

        assertThrows(InvalidParametersException.class,
                () -> feedbackResponseCommentsDb.updateFeedbackResponseComment(comment));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(comment), never());
    }

    @Test
    public void testUpdateComment_commentDoesNotExist_throwsEntityDoesNotExistException() {
        FeedbackResponseComment comment = getTypicalResponseComment();
        comment.setId(101L);

        assertThrows(EntityDoesNotExistException.class,
                () -> feedbackResponseCommentsDb.updateFeedbackResponseComment(comment));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(comment), never());
    }

    @Test
    public void testUpdateCourse_success() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackResponseComment comment = getTypicalResponseComment();
        comment.setCommentText("Placeholder Text");

        doReturn(comment).when(feedbackResponseCommentsDb).getFeedbackResponseComment(anyLong());
        feedbackResponseCommentsDb.updateFeedbackResponseComment(comment);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(comment));
    }

    private FeedbackResponseComment getTypicalResponseComment() {
        FeedbackResponseComment comment = new FeedbackResponseComment(null, "",
                FeedbackParticipantType.STUDENTS, null, null, "",
                false, false,
                null, null, null);
        comment.setId(TYPICAL_ID);
        return comment;
    }

}
