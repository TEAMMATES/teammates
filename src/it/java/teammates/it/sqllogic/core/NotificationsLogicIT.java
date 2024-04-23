package teammates.it.sqllogic.core;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.NotificationsLogic;
import teammates.storage.sqlentity.Notification;

/**
 * SUT: {@link NotificationsLogic}.
 */
public class NotificationsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private NotificationsLogic notificationsLogic = NotificationsLogic.inst();

    @Test
    public void testCreateNotification_endTimeIsBeforeStartTime_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(
                Instant.parse("2011-02-01T00:00:00Z"),
                Instant.parse("2011-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "A deprecation note",
                "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        assertNull(notificationsLogic.getNotification(invalidNotification.getId()));
    }

    @Test
    public void testCreateNotification_emptyTitle_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "",
                "<p>Deprecation happens in three minutes</p>");

        assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        assertNull(notificationsLogic.getNotification(invalidNotification.getId()));
    }

    @Test
    public void testCreateNotification_emptyMessage_throwsInvalidParametersException() {
        Notification invalidNotification = new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                "A deprecation note",
                "");

        assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        assertNull(notificationsLogic.getNotification(invalidNotification.getId()));
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
        notificationsLogic.createNotification(notification);

        UUID notificationId = notification.getId();
        Notification expectedNotification = notificationsLogic.updateNotification(notificationId, newStartTime, newEndTime,
                newStyle, newTargetUser, newTitle, newMessage);

        assertEquals(notificationId, expectedNotification.getId());
        assertEquals(newStartTime, expectedNotification.getStartTime());
        assertEquals(newEndTime, expectedNotification.getEndTime());
        assertEquals(newStyle, expectedNotification.getStyle());
        assertEquals(newTargetUser, expectedNotification.getTargetUser());
        assertEquals(newTitle, expectedNotification.getTitle());
        assertEquals(newMessage, expectedNotification.getMessage());

        Notification actualNotification = notificationsLogic.getNotification(notificationId);
        verifyEquals(expectedNotification, actualNotification);

        ______TS("failure: update notification that does not exist");
        UUID nonExistentId = generateDifferentUuid(notificationId);

        assertThrows(EntityDoesNotExistException.class, () -> notificationsLogic.updateNotification(nonExistentId,
                newStartTime, newEndTime, newStyle, newTargetUser, newTitle, newMessage));
    }

    @Test
    public void testUpdateNotification_invalidParameters_originalUnchanged() throws Exception {

        String originalTitle = "The original title";
        String originalMessage = "<p>Deprecation happens in three minutes</p>";
        Notification notif = new Notification(
                Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"),
                NotificationStyle.DANGER,
                NotificationTargetUser.GENERAL,
                originalTitle,
                originalMessage);
        notificationsLogic.createNotification(notif);

        String invalidLongTitle = "1234567890".repeat(10);
        String validNewMessage = "This should not be reflected.";
        assertThrows(
                InvalidParametersException.class,
                () -> notificationsLogic.updateNotification(
                        notif.getId(),
                        Instant.parse("2011-01-01T00:00:00Z"),
                        Instant.parse("2099-01-01T00:00:00Z"),
                        NotificationStyle.DANGER,
                        NotificationTargetUser.GENERAL,
                        invalidLongTitle,
                        validNewMessage));

        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
        Notification actual = notificationsLogic.getNotification(notif.getId());
        assertEquals(originalTitle, actual.getTitle());
        assertEquals(originalMessage, actual.getMessage());
    }

}
