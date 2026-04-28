package teammates.storage.sqlapi;

import static org.mockito.Mockito.doReturn;
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
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackSessionsDb}.
 */

public class FeedbackSessionsDbTest extends BaseTestCase {
    private FeedbackSessionsDb feedbackSessionsDb;
    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        feedbackSessionsDb = spy(FeedbackSessionsDb.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateSession_sessionDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(feedbackSession.getId());
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(
                feedbackSession.getName(), feedbackSession.getCourseId());

        feedbackSessionsDb.createFeedbackSession(feedbackSession);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), times(1));
    }

    @Test
    public void testCreateSession_duplicateSessionById_throwsEntityAlreadyExistsException() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        UUID uuid = feedbackSession.getId();
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(uuid);
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(
                feedbackSession.getName(), feedbackSession.getCourseId());

        assertThrows(EntityAlreadyExistsException.class,
                () -> feedbackSessionsDb.createFeedbackSession(feedbackSession));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), never());
    }

    @Test
    public void testCreateSession_duplicateSessionByNameAndCourse_throwsEntityAlreadyExistsException() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        UUID uuid = feedbackSession.getId();
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(uuid);
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(
                feedbackSession.getName(), feedbackSession.getCourseId());

        assertThrows(EntityAlreadyExistsException.class,
                () -> feedbackSessionsDb.createFeedbackSession(feedbackSession));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), never());
    }

    @Test
    public void testCreateSession_invalidParams_throwsInvalidParametersException() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        feedbackSession.setName("");

        assertThrows(InvalidParametersException.class, () -> feedbackSessionsDb.createFeedbackSession(feedbackSession));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), never());
    }

    @Test
    public void testCreateSession_nullParams_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> feedbackSessionsDb.createFeedbackSession(null));
    }

    @Test
    public void testGetFeedbackSession_sessionExists_success() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        UUID uuid = feedbackSession.getId();
        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackSession.class, uuid)).thenReturn(feedbackSession);

        FeedbackSession sessionFetched = feedbackSessionsDb.getFeedbackSession(uuid);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackSession.class, uuid), times(1));
        assertEquals(feedbackSession, sessionFetched);
    }

    @Test
    public void testGetFeedbackSession_sessionDoesNotExists_returnNull() {
        UUID randomUuid = UUID.randomUUID();
        mockHibernateUtil.when(() -> HibernateUtil.get(FeedbackSession.class, randomUuid)).thenReturn(null);

        FeedbackSession sessionFetched = feedbackSessionsDb.getFeedbackSession(randomUuid);

        mockHibernateUtil.verify(() -> HibernateUtil.get(FeedbackSession.class, randomUuid), times(1));
        assertNull(sessionFetched);
    }

    @Test
    public void testDeleteFeedbackSession_success() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());

        feedbackSessionsDb.deleteFeedbackSession(feedbackSession);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(feedbackSession), times(1));
    }
}
