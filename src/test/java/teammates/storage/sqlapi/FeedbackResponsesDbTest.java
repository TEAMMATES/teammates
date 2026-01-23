package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.List;
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
import teammates.storage.sqlentity.responses.FeedbackTextResponse;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackResponsesDb}.
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
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testGetFeedbackResponse_success() {
        FeedbackResponse fr = getTypicalFeedbackResponse();
        UUID id = fr.getId();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponse.class, id)).thenReturn(fr);

        FeedbackResponse result = feedbackResponsesDb.getFeedbackResponse(id);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackResponse.class, id), times(1));
        assertEquals(fr, result);
    }

    @Test
    public void testGetFeedbackResponse_feedbackResponseDoesNotExist_returnsNull() {
        UUID id = UUID.randomUUID();

        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackResponse.class, id)).thenReturn(null);

        FeedbackResponse result = feedbackResponsesDb.getFeedbackResponse(id);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackResponse.class, id), times(1));
        assertNull(result);
    }

    @Test
    public void testCreateFeedbackResponse_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponse fr = getTypicalFeedbackResponse();

        doReturn(null).when(feedbackResponsesDb).getFeedbackResponse(fr.getId());

        feedbackResponsesDb.createFeedbackResponse(fr);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(fr), times(1));
    }

    @Test
    public void testCreateFeedbackResponse_feedbackResponseAlreadyExists_throwsEntityAlreadyExistsException() {
        FeedbackResponse fr = getTypicalFeedbackResponse();

        doReturn(fr).when(feedbackResponsesDb).getFeedbackResponse(fr.getId());

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> feedbackResponsesDb.createFeedbackResponse(fr));

        assertEquals(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, fr.toString()), eaee.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(fr), never());
    }

    @Test
    public void testCreateFeedbackResponse_invalidFeedbackResponse_throwsInvalidParametersException() {
        FeedbackResponse fr = getInvalidFeedbackResponse();
        // Spy on the entity to force invalidity info, since the base implementation returns empty list
        FeedbackResponse spyFr = spy(fr);
        doReturn(List.of("Invalid response")).when(spyFr).getInvalidityInfo();

        UUID id = spyFr.getId();
        doReturn(null).when(feedbackResponsesDb).getFeedbackResponse(id);

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> feedbackResponsesDb.createFeedbackResponse(spyFr));

        assertEquals(spyFr.getInvalidityInfo(), List.of(ipe.getMessage()));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(spyFr), never());
    }

    @Test
    public void testUpdateFeedbackResponse_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackResponse fr = getTypicalFeedbackResponse();

        doReturn(fr).when(feedbackResponsesDb).getFeedbackResponse(fr.getId());
        mockHibernateUtil.when(() -> HibernateUtil.merge(fr)).thenReturn(fr);

        FeedbackResponse result = feedbackResponsesDb.updateFeedbackResponse(fr);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(fr), times(1));
        assertEquals(fr, result);
    }

    @Test
    public void testUpdateFeedbackResponse_invalidFeedbackResponse_throwsInvalidParametersException() {
        FeedbackResponse fr = getInvalidFeedbackResponse();
        // Spy on the entity to force invalidity info, since the base implementation returns empty list
        FeedbackResponse spyFr = spy(fr);
        doReturn(List.of("Invalid response")).when(spyFr).getInvalidityInfo();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> feedbackResponsesDb.updateFeedbackResponse(spyFr));

        assertEquals(spyFr.getInvalidityInfo(), List.of(ipe.getMessage()));
        mockHibernateUtil.verify(() -> HibernateUtil.merge(spyFr), never());
    }

    @Test
    public void testUpdateFeedbackResponse_feedbackResponseDoesNotExist_throwsEntityDoesNotExistException() {
        FeedbackResponse fr = getTypicalFeedbackResponse();

        doReturn(null).when(feedbackResponsesDb).getFeedbackResponse(fr.getId());

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> feedbackResponsesDb.updateFeedbackResponse(fr));

        assertEquals(ERROR_UPDATE_NON_EXISTENT, ednee.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.merge(fr), never());
    }

    @Test
    public void testDeleteFeedbackResponse_success() {
        FeedbackResponse fr = getTypicalFeedbackResponse();

        feedbackResponsesDb.deleteFeedbackResponse(fr);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(fr), times(1));
    }

    @Test
    public void testDeleteFeedbackResponse_nullFeedbackResponse_nothingHappens() {
        feedbackResponsesDb.deleteFeedbackResponse(null);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(any()), never());
    }

    /**
     * Creates a typical valid FeedbackResponse for testing.
     */
    private FeedbackResponse getTypicalFeedbackResponse() {
        return getTypicalFeedbackResponseForQuestion(
                getTypicalFeedbackQuestionForSession(
                        getTypicalFeedbackSessionForCourse(getTypicalCourse())));
    }

    /**
     * Creates an invalid FeedbackResponse for testing.
     * The response is invalid because it has null answer details.
     */
    private FeedbackResponse getInvalidFeedbackResponse() {
        FeedbackResponse fr = getTypicalFeedbackResponse();
        if (fr instanceof FeedbackTextResponse) {
            ((FeedbackTextResponse) fr).setAnswer(null);
        } else {
            fail("Test setup failure: Expected FeedbackTextResponse but got " + fr.getClass().getSimpleName());
        }
        return fr;
    }
}
