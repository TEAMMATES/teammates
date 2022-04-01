package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.NotificationsDb;

/**
 * SUT: {@link NotificationsLogic}.
 */
public class NotificationsLogicTest extends BaseLogicTest {
    private NotificationAttributes n;
    private final NotificationsDb notifDb = NotificationsDb.inst();
    private final NotificationsLogic notifLogic = NotificationsLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testAll() throws Exception {
        testGetNotification();
        testGetAllNotifications();
        testGetActiveNotificationsByTargetUser();
        testCreateNotification();
        testUpdateNotification();
        testDeleteNotification();
    }

    private void testGetNotification() throws Exception {
        ______TS("success: typical case");

        n = getTypicalNotification();
        notifDb.createEntity(n);

        NotificationAttributes retrievedNotif = notifLogic.getNotification(n.getNotificationId());
        verifyPresentInDatabase(retrievedNotif);

        removeNotificationsFromDb(n);

        ______TS("failure: Null parameter");

        assertThrows(AssertionError.class,
                () -> notifLogic.getNotification(null));

        ______TS("failure: notification doesn't exist");

        assertNull(notifLogic.getNotification("nonexistant-notification"));
    }

    private void testGetAllNotifications() throws Exception {
        ______TS("success: retrieve all when there exist notifications");

        n = getTypicalNotification();
        notifDb.createEntity(n);
        NotificationAttributes n1 = getTypicalNotification();
        n1.setNotificationId("new-notification-id");
        notifDb.createEntity(n1);

        List<NotificationAttributes> retreivedNotifs = notifLogic.getAllNotifications();
        retreivedNotifs.forEach(this::verifyPresentInDatabase);

        removeNotificationsFromDb(n, n1);
    }

    private void testGetActiveNotificationsByTargetUser() throws Exception {
        ______TS("success : valid target user");

        n = getTypicalNotification();
        n.setTargetUser(NotificationTargetUser.STUDENT);
        notifDb.createEntity(n);
        List<NotificationAttributes> retreivedNotifs =
                notifLogic.getActiveNotificationsByTargetUser(NotificationTargetUser.STUDENT);

        assertTrue(retreivedNotifs.contains(n));
        removeNotificationsFromDb(n);
    }

    private void testCreateNotification() throws Exception {
        ______TS("success: typical case");

        n = getTypicalNotification();
        notifLogic.createNotification(n);

        verifyPresentInDatabase(n);
        removeNotificationsFromDb(n);

        ______TS("failure: Null parameter");

        assertThrows(AssertionError.class,
                () -> notifLogic.createNotification(null));

        ______TS("failure: test create with invalid title name");

        n = getTypicalNotification();
        n.setTitle("");
        Exception e = assertThrows(Exception.class, () -> notifLogic.createNotification(n));
        assertEquals("The field 'notification title' is empty.", e.getMessage());
        verifyAbsentInDatabase(n);
    }

    private void testUpdateNotification() throws Exception {
        ______TS("success: typical case");

        n = dataBundle.notifications.get("notification1");

        NotificationAttributes.UpdateOptions update1 =
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withStyle(NotificationStyle.WARNING)
                        .withTargetUser(NotificationTargetUser.STUDENT)
                        .withTitle("The edited title")
                        .withMessage("The edited message")
                        .build();

        NotificationAttributes actual = notifLogic.updateNotification(update1);
        assertEquals(NotificationStyle.WARNING, actual.getStyle());
        assertEquals(NotificationTargetUser.STUDENT, actual.getTargetUser());
        assertEquals("The edited title", actual.getTitle());
        assertEquals("The edited message", actual.getMessage());

        ______TS("failure: invalid build options");

        n = dataBundle.notifications.get("notification1");

        NotificationAttributes.UpdateOptions update2 =
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle("")
                        .build();

        assertThrows(InvalidParametersException.class,
                () -> notifLogic.updateNotification(update2));

        ______TS("failure: invalid notification id");

        NotificationAttributes.UpdateOptions update3 =
                NotificationAttributes.updateOptionsBuilder("invalid-notification-id")
                        .withTitle("A valid title")
                        .build();

        assertThrows(EntityDoesNotExistException.class,
                () -> notifLogic.updateNotification(update3));
    }

    private void testDeleteNotification() {
        ______TS("success: delete corresponding notification");

        n = dataBundle.notifications.get("notification1");
        notifLogic.deleteNotification(n.getNotificationId());

        verifyAbsentInDatabase(n);

        ______TS("failure: invalid id");

        int expectedLength = notifDb.getAllNotifications().size();
        notifLogic.deleteNotification("invalid-id");
        int actualLength = notifDb.getAllNotifications().size();

        assertEquals(expectedLength, actualLength);
    }

    private NotificationAttributes getTypicalNotification() {
        return NotificationAttributes
                .builder("some-notif-id")
                .withStartTime(Instant.now())
                .withEndTime(Instant.now().plusSeconds(100))
                .withStyle(NotificationStyle.WARNING)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("Maintenance Notice")
                .withMessage("Maintenance at 3pm today")
                .build();
    }

    private void removeNotificationsFromDb(NotificationAttributes... notifications) {
        for (NotificationAttributes notif : notifications) {
            notifDb.deleteNotification(notif.getNotificationId());
        }
    }
}
