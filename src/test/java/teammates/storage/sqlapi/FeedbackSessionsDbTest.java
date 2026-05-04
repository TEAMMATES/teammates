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
    public void testCreateSession_sessionDoesNotExist_success() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());

        feedbackSessionsDb.createFeedbackSession(feedbackSession);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), times(1));
    }

    @Test
    public void testCreateSession_invalidParams_stillPersists() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        feedbackSession.setName("");

        feedbackSessionsDb.createFeedbackSession(feedbackSession);
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), times(1));
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
