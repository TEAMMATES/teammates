package teammates.it.storage.sqlapi;

import org.testng.annotations.Test;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.NotificationsDb;
import teammates.storage.sqlentity.Notification;

import java.time.Instant;
import java.util.UUID;

/**
 * SUT: {@link NotificationsDb}.
 */
public class NotificationDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final NotificationsDb notificationsDb = NotificationsDb.inst();

    @Test
    public void testCreateNotification() throws EntityAlreadyExistsException, InvalidParametersException {
        ______TS("success: create notification that does not exists");
        Notification newNotification = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();

        notificationsDb.createNotification(newNotification);

        UUID notificationId = newNotification.getNotificationId();
        Notification actualNotification = notificationsDb.getNotification(notificationId);
        verifyEquals(newNotification, actualNotification);

        ______TS("failure: create notification that already exists, exception thrown");
        Notification identicalNotification = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();
        identicalNotification.setNotificationId(notificationId);

        assertNotSame(newNotification, identicalNotification);
        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> notificationsDb.createNotification(identicalNotification));
        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + identicalNotification.toString());

        ______TS("failure: invalid parameter, start time is before end time");
        Notification invalidNotification1 = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-02-01T00:00:00Z"))
                .withEndTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification1));

        ______TS("failure: empty title");
        Notification invalidNotification2 = new Notification.NotificationBuilder("")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("<p>Deprecation happens in three minutes</p>")
                .build();

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification2));

        ______TS("failure: empty message");
        Notification invalidNotification3 = new Notification.NotificationBuilder("A deprecation note")
                .withStartTime(Instant.parse("2011-01-01T00:00:00Z"))
                .withEndTime(Instant.parse("2099-01-01T00:00:00Z"))
                .withStyle(NotificationStyle.DANGER)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withMessage("")
                .build();

        assertThrows(InvalidParametersException.class, () -> notificationsDb.createNotification(invalidNotification3));
    }
}
