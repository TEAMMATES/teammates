package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.storage.entity.Notification;
import teammates.test.BaseTestCaseWithDatabaseAccess;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationDbIT extends BaseTestCaseWithDatabaseAccess {

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test
    public void testPersistNotification() {
        Notification newNotification = generateTypicalNotification();

        inTransaction(() -> notificationsDb.persistNotification(newNotification));

        UUID notificationId = newNotification.getId();
        Notification actualNotification = inTransaction(() -> notificationsDb.getNotification(notificationId));
        assertEquals(newNotification, actualNotification);
    }

    @Test
    public void testGetNotification() {
        ______TS("success: get a notification that already exists");
        Notification newNotification = generateTypicalNotification();

        inTransaction(() -> notificationsDb.persistNotification(newNotification));

        UUID notificationId = newNotification.getId();
        Notification actualNotification = inTransaction(() -> notificationsDb.getNotification(notificationId));
        assertEquals(newNotification, actualNotification);

        ______TS("success: get a notification that does not exist");
        UUID nonExistentId = generateDifferentUuid(notificationId);
        Notification nonExistentNotification = inTransaction(() -> notificationsDb.getNotification(nonExistentId));
        assertNull(nonExistentNotification);
    }

    @Test
    public void testDeleteNotification() {
        ______TS("success: delete a notification that already exists");
        Notification notification = generateTypicalNotification();

        inTransaction(() -> notificationsDb.persistNotification(notification));
        UUID notificationId = notification.getId();
        assertNotNull(inTransaction(() -> notificationsDb.getNotification(notificationId)));

        inTransaction(() -> notificationsDb.deleteNotification(notification));
        assertNull(inTransaction(() -> notificationsDb.getNotification(notificationId)));
    }

    @Test
    public void testGetActiveNotificationsByTargetUser() {
        Notification n1 = new Notification(
                Instant.parse("2011-01-04T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "notification 1",
                "<p>message 1</p>");
        Notification n2 = new Notification(
                Instant.parse("2011-01-02T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.INSTRUCTOR,
                "notification 2",
                "<p>message 2</p>");
        Notification n3 = new Notification(
                Instant.parse("2011-01-03T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.STUDENT,
                "notification 3",
                "<p>message 3</p>");
        Notification n4 = new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "notification 4",
                "<p>message 4</p>");
        Notification n5 = new Notification(
                Instant.parse("2011-01-05T00:00:00Z"),
                Instant.parse("2012-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "notification 5",
                "<p>message 5</p>");
        Notification n6 = new Notification(
                Instant.parse("2011-01-05T00:00:00Z"),
                Instant.parse("2013-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.INSTRUCTOR,
                "notification 6",
                "<p>message 6</p>");

        List<Notification> allNotifications = List.of(n1, n2, n3, n4, n5, n6);
        inTransaction(() -> {
            for (Notification n : allNotifications) {
                notificationsDb.persistNotification(n);
            }
        });

        ______TS("success: get active notifications with target user GENERAL");
        List<Notification> actualNotifications =
                inTransaction(() -> notificationsDb.getNotificationsByTargetUsers(
                        List.of(NotificationTargetUser.GENERAL), true));
        List<Notification> expectedNotifications = List.of(n4, n1);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it1 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            assertEquals(it1.next(), actual);
        });

        ______TS("success: get active notifications with target users INSTRUCTOR and GENERAL");
        actualNotifications = inTransaction(() -> notificationsDb.getNotificationsByTargetUsers(
                List.of(NotificationTargetUser.INSTRUCTOR, NotificationTargetUser.GENERAL), true));
        expectedNotifications = List.of(n4, n2, n1);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it2 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            assertEquals(it2.next(), actual);
        });

        ______TS("success: get active notifications with target user INSTRUCTOR only");
        actualNotifications = inTransaction(() -> notificationsDb.getNotificationsByTargetUsers(
                List.of(NotificationTargetUser.INSTRUCTOR), true));
        expectedNotifications = List.of(n2);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it3 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            assertEquals(it3.next(), actual);
        });
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
