package teammates.it.storage.sqlapi;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
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

        UUID notificationId = newNotification.getId();
        Notification actualNotification = notificationsDb.getNotification(notificationId);
        verifyEquals(newNotification, actualNotification);
    }

    @Test
    public void testGetNotification() throws EntityAlreadyExistsException, InvalidParametersException {
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
    public void testDeleteNotification() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: delete a notification that already exists");
        Notification notification = generateTypicalNotification();

        notificationsDb.createNotification(notification);
        UUID notificationId = notification.getId();
        assertNotNull(notificationsDb.getNotification(notificationId));

        notificationsDb.deleteNotification(notification);
        assertNull(notificationsDb.getNotification(notificationId));
    }

    @Test
    public void testGetAllNotifications() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: no notification present in the database");
        List<Notification> allNotifications = notificationsDb.getAllNotifications();
        assertEquals(0, allNotifications.size());

        ______TS("success: multiple notifications present in the database");
        Notification n1 = generateTypicalNotification();
        Notification n2 = generateTypicalNotification();

        notificationsDb.createNotification(n1);
        notificationsDb.createNotification(n2);

        allNotifications = notificationsDb.getAllNotifications();

        assertEquals(2, allNotifications.size());
        verifyEquals(n1, allNotifications.get(0));
        verifyEquals(n2, allNotifications.get(1));
    }

    @Test
    public void testGetActiveNotificationsByTargetUser() throws EntityAlreadyExistsException, InvalidParametersException {
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

        ______TS("success: get active notification with target user GENERAL");
        List<Notification> actualNotifications =
                notificationsDb.getActiveNotificationsByTargetUser(NotificationTargetUser.GENERAL);
        List<Notification> expectedNotifications = List.of(n4, n1);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it1 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            verifyEquals(it1.next(), actual);
        });

        ______TS("success: get active notification with target user INSTRUCTOR");
        actualNotifications = notificationsDb.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR);
        expectedNotifications = List.of(n4, n2, n1);
        assertEquals(expectedNotifications.size(), actualNotifications.size());
        Iterator<Notification> it2 = expectedNotifications.iterator();
        actualNotifications.forEach(actual -> {
            verifyEquals(it2.next(), actual);
        });
    }

    @Test
    public void testCreateNotification_sqlInjectionAttemptIntoTitle_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = generateTypicalNotification();
        // insert into notifications (created_at, end_time, message, shown, start_time, style, target_user, title,
        // updated_at, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        String sqlInjectionTitle = "t', '101231 2359'::timestamp, uuid_generate_v4()); "
                + "DROP TABLE notifications;--";
        notification.setTitle(sqlInjectionTitle);
        UUID id = notificationsDb.createNotification(notification).getId();
        Notification createdNotification = notificationsDb.getNotification(id);
        assertNotNull(createdNotification);
        assertEquals(sqlInjectionTitle, createdNotification.getTitle());
    }

    @Test
    public void testCreateNotification_sqlInjectionAttemptIntoMessage_shouldNotRunSqlInjectionQuery()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Notification notification = generateTypicalNotification();
        // insert into notifications (created_at, end_time, message, shown, start_time, style, target_user, title,
        // updated_at, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        String sqlInjectionMessage = "m', TRUE, '110101 0000'::timestamp, 'DANGER', 'GENERAL', 't', "
                + "'101231 2359'::timestamp, uuid_generate_v4()); DROP TABLE notifications;--";
        notification.setMessage(sqlInjectionMessage);
        UUID id = notificationsDb.createNotification(notification).getId();
        Notification createdNotification = notificationsDb.getNotification(id);
        assertNotNull(createdNotification);
        assertEquals(SanitizationHelper.sanitizeForRichText(sqlInjectionMessage), createdNotification.getMessage());
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
