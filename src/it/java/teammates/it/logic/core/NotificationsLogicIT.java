package teammates.it.logic.core;

import org.junit.jupiter.api.Assertions;
import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.NotificationsLogic;
import teammates.storage.entity.Notification;

/**
 * SUT: {@link NotificationsLogic}.
 */
public class NotificationsLogicIT extends BaseTestCaseWithDatabaseAccess {

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

        Assertions.assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        Assertions.assertNull(notificationsLogic.getNotification(invalidNotification.getId()));
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

        Assertions.assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        Assertions.assertNull(notificationsLogic.getNotification(invalidNotification.getId()));
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

        Assertions.assertThrows(InvalidParametersException.class, () -> notificationsLogic.createNotification(invalidNotification));
        Assertions.assertNull(notificationsLogic.getNotification(invalidNotification.getId()));
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

        Assertions.assertEquals(notificationId, expectedNotification.getId());
        Assertions.assertEquals(newStartTime, expectedNotification.getStartTime());
        Assertions.assertEquals(newEndTime, expectedNotification.getEndTime());
        Assertions.assertEquals(newStyle, expectedNotification.getStyle());
        Assertions.assertEquals(newTargetUser, expectedNotification.getTargetUser());
        Assertions.assertEquals(newTitle, expectedNotification.getTitle());
        Assertions.assertEquals(newMessage, expectedNotification.getMessage());

        Notification actualNotification = notificationsLogic.getNotification(notificationId);
        verifyEquals(expectedNotification, actualNotification);

        ______TS("failure: update notification that does not exist");
        UUID nonExistentId = generateDifferentUuid(notificationId);

        Assertions.assertThrows(EntityDoesNotExistException.class, () -> notificationsLogic.updateNotification(nonExistentId,
                newStartTime, newEndTime, newStyle, newTargetUser, newTitle, newMessage));
    }
}
