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
    public void testCreateNotification_invalidNonNullParameters() {
        ______TS("failure: start time is before end time");
        Notification invalidNotification1 = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-02-01T00:00:00Z"))
                .withEndTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        UUID notificationId = UUID.randomUUID();
        invalidNotification1.setNotificationId(notificationId);
        when(session.get(Notification.class, notificationId.toString())).thenReturn(invalidNotification1);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification1));
        verify(session, never()).persist(invalidNotification1);

        ______TS("failure: empty title");
        Notification invalidNotification2 = new Notification.NotificationBuilder("")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        notificationId = UUID.randomUUID();
        invalidNotification2.setNotificationId(notificationId);
        when(session.get(Notification.class, notificationId.toString())).thenReturn(invalidNotification2);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification2));
        verify(session, never()).persist(invalidNotification2);

        ______TS("failure: empty message");
        Notification invalidNotification3 = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("")
                .build();
        notificationId = UUID.randomUUID();
        invalidNotification3.setNotificationId(notificationId);
        when(session.get(Notification.class, notificationId.toString())).thenReturn(invalidNotification3);

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification3));
        verify(session, never()).persist(invalidNotification3);
    }
}
