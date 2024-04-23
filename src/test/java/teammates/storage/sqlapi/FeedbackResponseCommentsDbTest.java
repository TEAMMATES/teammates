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

    private static final Long NOT_TYPICAL_ID = 101L;
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
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        feedbackResponseCommentsDb.createFeedbackResponseComment(comment);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(comment));
    }

    @Test
    public void testCreateComment_commentAlreadyExists_throwsEntityAlreadyExistsException() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, TYPICAL_ID)).thenReturn(comment);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> feedbackResponseCommentsDb.createFeedbackResponseComment(comment));

        assertEquals("Trying to create an entity that exists: " + comment.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(comment), never());
    }

    @Test
    public void testGetComment_commentAlreadyExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, TYPICAL_ID)).thenReturn(comment);

        FeedbackResponseComment commentFetched = feedbackResponseCommentsDb.getFeedbackResponseComment(TYPICAL_ID);

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, TYPICAL_ID)).thenReturn(comment);
        assertEquals(comment, commentFetched);
    }

    @Test
    public void testGetComment_commentDoesNotExist_returnsNull() {
        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, NOT_TYPICAL_ID)).thenReturn(null);

        FeedbackResponseComment commentFetched = feedbackResponseCommentsDb.getFeedbackResponseComment(NOT_TYPICAL_ID);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackResponseComment.class, NOT_TYPICAL_ID), times(1));
        assertNull(commentFetched);
    }

    @Test
    public void testDeleteComment_commentExists_success() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponseComment.class, TYPICAL_ID)).thenReturn(comment);
        feedbackResponseCommentsDb.deleteFeedbackResponseComment(TYPICAL_ID);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(comment));
    }

    @Test
    public void testUpdateComment_commentInvalid_throwsInvalidParametersException() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);
        comment.setGiverType(FeedbackParticipantType.SELF);

        assertThrows(InvalidParametersException.class,
                () -> feedbackResponseCommentsDb.updateFeedbackResponseComment(comment));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(comment), never());
    }

    @Test
    public void testUpdateComment_commentDoesNotExist_throwsEntityDoesNotExistException() {
        FeedbackResponseComment comment = getTypicalResponseComment(NOT_TYPICAL_ID);

        assertThrows(EntityDoesNotExistException.class,
                () -> feedbackResponseCommentsDb.updateFeedbackResponseComment(comment));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(comment), never());
    }

    @Test
    public void testUpdateCourse_success() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);
        comment.setCommentText("Placeholder Text");

        doReturn(comment).when(feedbackResponseCommentsDb).getFeedbackResponseComment(anyLong());
        mockHibernateUtil.when(() -> HibernateUtil.merge(comment)).thenReturn(comment);
        feedbackResponseCommentsDb.updateFeedbackResponseComment(comment);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(comment));
    }

}
