package teammates.it.storage.sqlapi;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
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
        Notification newNotification = new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "A deprecation note",
                "<p>Deprecation happens in three minutes</p>");

        notificationsDb.createNotification(newNotification);

        UUID notificationId = newNotification.getNotificationId();
        Notification actualNotification = notificationsDb.getNotification(notificationId);
        verifyEquals(newNotification, actualNotification);
    }

    @Test
    public void testGetNotification() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: get a notification that already exists");
        Notification newNotification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");

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
    public void testUpdateNotification()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Instant newStartTime = Instant.parse("2012-01-01T00:00:00Z");
        Instant newEndTime = Instant.parse("2098-01-01T00:00:00Z");
        NotificationStyle newStyle = NotificationStyle.DARK;
        NotificationTargetUser newTargetUser = NotificationTargetUser.INSTRUCTOR;
        String newTitle = "An updated deprecation note";
        String newMessage = "<p>Deprecation happens in three seconds</p>";

        ______TS("success: update notification that already exists");
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notificationsDb.createNotification(notification);

        UUID notificationId = notification.getNotificationId();
        Notification expectedNotification = notificationsDb.updateNotification(notificationId, newStartTime, newEndTime,
                newStyle, newTargetUser, newTitle, newMessage);

        assertEquals(notificationId, expectedNotification.getNotificationId());
        assertEquals(newStartTime, expectedNotification.getStartTime());
        assertEquals(newEndTime, expectedNotification.getEndTime());
        assertEquals(newStyle, expectedNotification.getStyle());
        assertEquals(newTargetUser, expectedNotification.getTargetUser());
        assertEquals(newTitle, expectedNotification.getTitle());
        assertEquals(newMessage, expectedNotification.getMessage());

        Notification actualNotification = notificationsDb.getNotification(notificationId);
        assertEquals(expectedNotification, actualNotification);

        ______TS("failure: update notification that does not exist");
        UUID nonExistentId = generateDifferentUuid(notificationId);

        assertThrows(EntityDoesNotExistException.class, () -> notificationsDb.updateNotification(nonExistentId,
                newStartTime, newEndTime, newStyle, newTargetUser, newTitle, newMessage));
    }
}
