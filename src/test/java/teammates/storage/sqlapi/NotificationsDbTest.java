package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;

import java.time.Instant;
import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationsDbTest extends BaseTestCase {

    private NotificationsDb notificationsDb = NotificationsDb.inst();

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateNotification_notificationDoesNotExist_success() {
        Notification newNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");

        notificationsDb.createNotification(newNotification);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newNotification));
    }

    @Test
    public void testGetNotification_success() {
        Notification notification = getTypicalNotificationWithId();
        mockHibernateUtil.when(() ->
                HibernateUtil.get(Notification.class, notification.getId())).thenReturn(notification);

        Notification actualNotification = notificationsDb.getNotification(notification.getId());

        mockHibernateUtil.verify(() -> HibernateUtil.get(Notification.class, notification.getId()));
        assertEquals(notification, actualNotification);
    }

    @Test
    public void testGetNotification_entityDoesNotExist() {
        UUID nonExistentId = UUID.fromString("00000000-0000-1000-0000-000000000000");
        mockHibernateUtil.when(() -> HibernateUtil.get(Notification.class, nonExistentId)).thenReturn(null);

        Notification actualNotification = notificationsDb.getNotification(nonExistentId);

        mockHibernateUtil.verify(() -> HibernateUtil.get(Notification.class, nonExistentId));
        assertNull(actualNotification);
    }

    @Test
    public void testDeleteNotification_entityExists_success() {
        Notification notification = getTypicalNotificationWithId();
        notificationsDb.deleteNotification(notification);
        mockHibernateUtil.verify(() -> HibernateUtil.remove(notification));
    }

    @Test
    public void testDeleteReadNotification_success() {
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        ReadNotification readNotification = new ReadNotification(account, notification);
        notificationsDb.deleteReadNotification(readNotification);
        mockHibernateUtil.verify(() -> HibernateUtil.remove(readNotification));
    }
}
