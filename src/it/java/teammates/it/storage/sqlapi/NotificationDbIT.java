package teammates.it.storage.sqlapi;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.NotificationsDb;
import teammates.storage.sqlentity.Notification;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test
    public void testCreateNotification() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: create notification that does not exist");
        Notification newNotification = generateTypicalNotification();

        notificationsDb.createNotification(newNotification);

        UUID notificationId = newNotification.getNotificationId();
        Notification actualNotification = notificationsDb.getNotification(notificationId);
        verifyEquals(newNotification, actualNotification);
    }

    @Test
    public void testGetNotification() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: get a notification that already exists");
        Notification newNotification = generateTypicalNotification();

        notificationsDb.createNotification(newNotification);

        UUID notificationId = newNotification.getNotificationId();
        Notification actualNotification = notificationsDb.getNotification(notificationId);
        verifyEquals(newNotification, actualNotification);

        ______TS("success: get a notification that does not exist");
        UUID nonExistentId = generateDifferentUuid(notificationId);
        Notification nonExistentNotification = notificationsDb.getNotification(nonExistentId);
        assertNull(nonExistentNotification);
    }

    @Test
    public void testDeleteNotification() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: delete a notification that already exists");
        Notification notification = generateTypicalNotification();

        notificationsDb.createNotification(notification);
        UUID notificationId = notification.getNotificationId();
        assertNotNull(notificationsDb.getNotification(notificationId));

        notificationsDb.deleteNotification(notification);
        assertNull(notificationsDb.getNotification(notificationId));
    }

    private Notification generateTypicalNotification() {
        return new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "A deprecation note",
                "<p>Deprecation happens in three minutes</p>");
    }
}
