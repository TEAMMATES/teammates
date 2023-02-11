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
    public void testCreateNotification_success() throws EntityAlreadyExistsException, InvalidParametersException {
        Notification newNotification = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        UUID notificationId = UUID.randomUUID();
        newNotification.setNotificationId(notificationId);
        when(session.get(Notification.class, notificationId.toString())).thenReturn(null);

        notificationsDb.createNotification(newNotification);

        verify(session, times(1)).persist(newNotification);
    }

    @Test
    public void testCreateNotification_invalidNonNullParameters_endTimeIsBeforeStartTime() {
        Notification invalidNotification = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-02-01T00:00:00Z"))
                .withEndTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        UUID notificationId = UUID.randomUUID();
        invalidNotification.setNotificationId(notificationId);
        when(session.get(Notification.class, notificationId.toString())).thenReturn(invalidNotification);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        verify(session, never()).persist(invalidNotification);
    }

    @Test
    public void testCreateNotification_invalidNonNullParameters_emptyTitle() {
        Notification invalidNotification = new Notification.NotificationBuilder("")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        UUID notificationId = UUID.randomUUID();
        invalidNotification.setNotificationId(notificationId);
        when(session.get(Notification.class, notificationId.toString())).thenReturn(invalidNotification);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        verify(session, never()).persist(invalidNotification);
    }

    @Test
    public void testCreateNotification_invalidNonNullParameters_emptyMessage() {
        Notification invalidNotification = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("")
                .build();
        UUID notificationId = UUID.randomUUID();
        invalidNotification.setNotificationId(notificationId);
        when(session.get(Notification.class, notificationId.toString())).thenReturn(invalidNotification);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification));
        verify(session, never()).persist(invalidNotification);
    }
}
