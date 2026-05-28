package teammates.it.storage.api;

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
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.storage.api.NotificationsDb;
import teammates.storage.entity.Notification;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationDbIT extends BaseTestCaseWithDatabaseAccess {

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test
    public void testCreateNotification() {
        ______TS("success: create notification that does not exist");
        Notification newNotification = generateTypicalNotification();

        notificationsDb.createNotification(newNotification);

        UUID notificationId = newNotification.getId();
        Notification actualNotification = notificationsDb.getNotification(notificationId);
        verifyEquals(newNotification, actualNotification);
    }

    @Test
    public void testGetNotification() {
        ______TS("success: get a notification that already exists");
        Notification newNotification = generateTypicalNotification();

        notificationsDb.createNotification(newNotification);

        UUID notificationId = newNotification.getId();
        Notification actualNotification = notificationsDb.getNotification(notificationId);
        verifyEquals(newNotification, actualNotification);

        ______TS("success: get a notification that does not exist");
        UUID nonExistentId = generateDifferentUuid(notificationId);
        Notification nonExistentNotification = notificationsDb.getNotification(nonExistentId);
        assertNull(nonExistentNotification);
    }

    @Test
    public void testDeleteNotification() {
        ______TS("success: delete a notification that already exists");
        Notification notification = generateTypicalNotification();

        notificationsDb.createNotification(notification);
        UUID notificationId = notification.getId();
        assertNotNull(notificationsDb.getNotification(notificationId));

        notificationsDb.deleteNotification(notification);
        assertNull(notificationsDb.getNotification(notificationId));
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
        for (Notification n : allNotifications) {
            notificationsDb.createNotification(n);
        }

        ______TS("success: get active notifications with target user GENERAL");
        List<Notification> actualNotifications =
                notificationsDb.getNotificationsByTargetUsers(List.of(NotificationTargetUser.GENERAL), true);
        List<Notification> expectedNotifications = List.of(n4, n1);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it1 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            verifyEquals(it1.next(), actual);
        });

        ______TS("success: get active notifications with target users INSTRUCTOR and GENERAL");
        actualNotifications = notificationsDb.getNotificationsByTargetUsers(
                List.of(NotificationTargetUser.INSTRUCTOR, NotificationTargetUser.GENERAL), true);
        expectedNotifications = List.of(n4, n2, n1);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it2 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            verifyEquals(it2.next(), actual);
        });

        ______TS("success: get active notifications with target user INSTRUCTOR only");
        actualNotifications = notificationsDb.getNotificationsByTargetUsers(
                List.of(NotificationTargetUser.INSTRUCTOR), true);
        expectedNotifications = List.of(n2);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it3 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            verifyEquals(it3.next(), actual);
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
