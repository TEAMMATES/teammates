package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackResponseCommentsDb}.
 */

public class FeedbackResponseCommentsDbTest extends BaseTestCase {

    private static final UUID TYPICAL_ID = UUID.fromString("00000000-0000-4000-8000-000000000064");

    private static final UUID NOT_TYPICAL_ID = UUID.fromString("00000000-0000-4000-8000-000000000065");
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
    public void testCreateComment_commentDoesNotExist_success() {
        FeedbackResponseComment comment = getTypicalResponseComment(TYPICAL_ID);

        feedbackResponseCommentsDb.createFeedbackResponseComment(comment);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(comment));
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

        feedbackResponseCommentsDb.deleteFeedbackResponseComment(comment);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(comment));
    }

}
