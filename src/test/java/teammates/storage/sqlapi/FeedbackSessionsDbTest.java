package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
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
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TimeHelperExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code FeedbackSessionsDb}.
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

        feedbackSessionsDb.createFeedbackSession(feedbackSession);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), times(1));
    }

    @Test
    public void testCreateSession_duplicateSession_throwsEntityAlreadyExistsException()
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        UUID uuid = feedbackSession.getId();
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(uuid);

        assertThrows(EntityAlreadyExistsException.class,
                () -> feedbackSessionsDb.createFeedbackSession(feedbackSession));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), never());
    }

    @Test
    public void testCreateSession_invalidParams_throwsInvalidParametersException()
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        feedbackSession.setName("");

        assertThrows(InvalidParametersException.class, () -> feedbackSessionsDb.createFeedbackSession(feedbackSession));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(feedbackSession), never());
    }

    @Test
    public void testCreateSession_nullParams_throwsAssertionError()
            throws InvalidParametersException, EntityAlreadyExistsException {
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
    public void testUpdateFeedbackSession_success() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(any(UUID.class));
        mockHibernateUtil.when(() -> HibernateUtil.merge(feedbackSession)).thenReturn(feedbackSession);

        feedbackSessionsDb.updateFeedbackSession(feedbackSession);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackSession), times(1));
    }

    @Test
    public void testUpdateFeedbackSession_sessionDoesNotExist_throwsEntityDoesNotExistException()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        UUID uuid = feedbackSession.getId();
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(uuid);

        assertThrows(EntityDoesNotExistException.class,
                () -> feedbackSessionsDb.updateFeedbackSession(feedbackSession));
        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackSession), never());
    }

    @Test
    public void testUpdateFeedbackSession_sessionInvalid_throwsInvalidParametersException()
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        UUID uuid = feedbackSession.getId();
        feedbackSession.setName("");
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(uuid);

        assertThrows(InvalidParametersException.class, () -> feedbackSessionsDb.updateFeedbackSession(feedbackSession));
        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackSession), never());
    }

    @Test
    public void testDeleteFeedbackSession_success() throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());

        feedbackSessionsDb.deleteFeedbackSession(feedbackSession);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(feedbackSession), times(1));
    }

    @Test
    public void testGetSoftDeletedFeedbackSession_isSoftDeleted_success() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        String sessionName = feedbackSession.getName();
        String courseId = feedbackSession.getCourse().getId();
        feedbackSession.setDeletedAt(TimeHelperExtension.getInstantDaysOffsetFromNow(2));
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(sessionName, courseId);

        FeedbackSession sessionFetched = feedbackSessionsDb.getSoftDeletedFeedbackSession(sessionName, courseId);

        assertEquals(feedbackSession, sessionFetched);
    }

    @Test
    public void testGetSoftDeletedFeedbackSession_notSoftDeleted_returnNull() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        String sessionName = feedbackSession.getName();
        String courseId = feedbackSession.getCourse().getId();
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(sessionName, courseId);

        FeedbackSession sessionFetched = feedbackSessionsDb.getSoftDeletedFeedbackSession(sessionName, courseId);

        assertNull(sessionFetched);
    }

    @Test
    public void testGetSoftDeletedFeedbackSession_sessionDoesNotExist_returnNull() {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        String sessionName = feedbackSession.getName();
        String courseId = feedbackSession.getCourse().getId();
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(sessionName, courseId);

        FeedbackSession sessionFetched = feedbackSessionsDb.getSoftDeletedFeedbackSession(sessionName, courseId);

        assertNull(sessionFetched);
    }

    @Test
    public void testRestoreDeletedFeedbackSession_success() throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        String sessionName = feedbackSession.getName();
        String courseId = feedbackSession.getCourse().getId();
        feedbackSession.setDeletedAt(TimeHelperExtension.getInstantDaysOffsetFromNow(2));
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(sessionName, courseId);
        mockHibernateUtil.when(() -> HibernateUtil.merge(feedbackSession)).thenReturn(feedbackSession);

        feedbackSessionsDb.restoreDeletedFeedbackSession(sessionName, courseId);

        assertNull(feedbackSession.getDeletedAt());
        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackSession), times(1));
    }

    @Test
    public void testRestoreDeletedFeedbackSession_sessionDoesNotExist_throwsEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        String sessionName = feedbackSession.getName();
        String courseId = feedbackSession.getCourse().getId();
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(sessionName, courseId);

        assertThrows(EntityDoesNotExistException.class,
                () -> feedbackSessionsDb.restoreDeletedFeedbackSession(sessionName, courseId));
        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackSession), never());
    }

    @Test
    public void testSoftDeleteFeedbackSession_success() throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        String sessionName = feedbackSession.getName();
        String courseId = feedbackSession.getCourse().getId();
        doReturn(feedbackSession).when(feedbackSessionsDb).getFeedbackSession(sessionName, courseId);
        mockHibernateUtil.when(() -> HibernateUtil.merge(feedbackSession)).thenReturn(feedbackSession);

        feedbackSessionsDb.softDeleteFeedbackSession(sessionName, courseId);

        assertNotNull(feedbackSession.getDeletedAt());
        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackSession), times(1));
    }

    @Test
    public void testSoftDeleteFeedbackSession_sessionDoesNotExist_throwsEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        String sessionName = feedbackSession.getName();
        String courseId = feedbackSession.getCourse().getId();
        doReturn(null).when(feedbackSessionsDb).getFeedbackSession(sessionName, courseId);

        assertThrows(EntityDoesNotExistException.class,
                () -> feedbackSessionsDb.restoreDeletedFeedbackSession(sessionName, courseId));
        mockHibernateUtil.verify(() -> HibernateUtil.merge(feedbackSession), never());
    }
}
