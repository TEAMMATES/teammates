package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbTest extends BaseTestCase {

    private FeedbackResponsesDb feedbackResponsesDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        feedbackResponsesDb = spy(FeedbackResponsesDb.class);
    }

    @AfterMethod
    public void tearDownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateFeedbackResponse_feedbackResponseDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponse feedbackResponse = getTypicalFeedbackResponse();

        feedbackResponsesDb.createFeedbackResponse(feedbackResponse);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackResponse));
    }

    @Test
    public void testCreateFeedbackResponse_feedbackResponseAlreadyExists_throwsEntityAlreadyExistsException() {
        FeedbackResponse feedbackResponse = getTypicalFeedbackResponse();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponse.getId()))
                .thenReturn(feedbackResponse);

        assertThrows(EntityAlreadyExistsException.class, () -> feedbackResponsesDb.createFeedbackResponse(feedbackResponse));

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponse.getId()));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackResponse), never());
    }

    @Test
    public void testGetFeedbackResponse_feedbackResponseExists_success() {
        FeedbackResponse feedbackResponse = getTypicalFeedbackResponse();
        UUID feedbackResponseId = feedbackResponse.getId();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponseId))
                .thenReturn(feedbackResponse);
        FeedbackResponse actualFeedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackResponseId);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponseId), times(1));
        assertEquals(feedbackResponse, actualFeedbackResponse);
    }

    @Test
    public void testGetFeedbackResponse_feedbackResponseDoesNotExist_returnsNull() {
        UUID feedbackResponseId = UUID.randomUUID();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponseId)).thenReturn(null);
        FeedbackResponse actualFeedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackResponseId);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponseId), times(1));
        assertNull(actualFeedbackResponse);
    }

    @Test
    public void testUpdateFeedbackResponse_feedbackResponseExists_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackResponse feedbackResponse = getTypicalFeedbackResponse();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponse.getId()))
                .thenReturn(feedbackResponse);
        feedbackResponsesDb.updateFeedbackResponse(feedbackResponse);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackResponse));
    }

    @Test
    public void testUpdateFeedbackResponse_feedbackResponseDoesNotExist_throwsEntityDoesNotExistException() {
        FeedbackResponse feedbackResponse = getTypicalFeedbackResponse();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponse.class, feedbackResponse.getId()))
                .thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> feedbackResponsesDb.updateFeedbackResponse(feedbackResponse));
        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackResponse), never());
    }

    @Test
    public void testDeleteFeedbackResponse_feedbackResponseExists_success() {
        FeedbackResponse feedbackResponse = getTypicalFeedbackResponse();

        feedbackResponsesDb.deleteFeedbackResponse(feedbackResponse);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(feedbackResponse));
    }

    @Test
    public void testDeleteFeedbackResponse_feedbackResponseDoesNotExist_silentlyFails() {
        feedbackResponsesDb.deleteFeedbackResponse(null);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(any()), never());
    }

}
