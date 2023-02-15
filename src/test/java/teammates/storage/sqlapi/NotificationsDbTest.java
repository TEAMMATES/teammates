package teammates.storage.sqlapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Notification;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code NotificationsDb}.
 */
public class NotificationsDbTest extends BaseTestCase {

    private NotificationsDb notificationsDb = NotificationsDb.inst();

    private Session session;

    @BeforeMethod
    public void setUp() {
        session = mock(Session.class);
        SessionFactory sessionFactory = mock(SessionFactory.class);
        HibernateUtil.setSessionFactory(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testCreateNotification_notificationDoesNotExist_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification newNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");

        notificationsDb.createNotification(newNotification);

        verify(session, times(1)).persist(newNotification);
    }

    @Test
    public void testCreateNotification_endTimeIsBeforeStartTime_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(Instant.parse("2011-02-01T00:00:00Z"),
                Instant.parse("2011-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        verify(session, never()).persist(invalidNotification);
    }

    @Test
    public void testCreateNotification_emptyTitle_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "", "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        verify(session, never()).persist(invalidNotification);
    }

    @Test
    public void testCreateNotification_emptyMessage_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "");

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        verify(session, never()).persist(invalidNotification);
    }

    @Test
    public void testGetNotification_success() throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = new Notification.NotificationBuilder()
                .withNotificationId(UUID.randomUUID())
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withTitle("A deprecation note")
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        notificationsDb.createNotification(notification);
        verify(session, times(1)).persist(notification);

        when(session.get(Notification.class, notification.getNotificationId())).thenReturn(notification);
        Notification actualNotification = notificationsDb.getNotification(notification.getNotificationId());

        assertEquals(notification, actualNotification);
    }

    @Test
    public void testGetNotification_entityDoesNotExist() throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = new Notification.NotificationBuilder()
                .withNotificationId(UUID.randomUUID())
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTitle("A deprecation note")
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        notificationsDb.createNotification(notification);
        verify(session, times(1)).persist(notification);

        UUID nonExistentId = UUID.fromString("00000000-0000-1000-0000-000000000000");

        when(session.get(Notification.class, nonExistentId)).thenReturn(null);
        Notification actualNotification = notificationsDb.getNotification(nonExistentId);

        assertNull(actualNotification);
    }
}
