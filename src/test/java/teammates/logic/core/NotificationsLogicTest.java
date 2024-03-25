package teammates.logic.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
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
    @SuppressWarnings("PMD.FinalFieldCouldBeStatic")
    private final Map<String, NotificationAttributes> typicalNotifications = getTypicalDataBundle().notifications;

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
        // include all tests that do not modify the database.
        // tests such as create, delete and update are tested separately.
        testGetNotification();
        testGetAllNotifications();
        testGetActiveNotificationsByTargetUser();
        testDoesNotificationExists();
    }

    @Test
    public void testCreateNotification() throws Exception {
        ______TS("success: typical case");

        n = getNewNotificationAttributes();
        notifLogic.createNotification(n);

        verifyPresentInDatabase(n);

        ______TS("failure: duplicate notification with the same ID");

        assertThrows(EntityAlreadyExistsException.class, () -> notifLogic.createNotification(n));

        removeNotificationsFromDb(n);

        ______TS("failure: Null parameter");

        assertThrows(AssertionError.class,
                () -> notifLogic.createNotification(null));

        ______TS("failure: test create with invalid title name");

        n = getNewNotificationAttributes();
        n.setTitle("");
        Exception e = assertThrows(Exception.class, () -> notifLogic.createNotification(n));
        assertEquals("The field 'notification title' is empty.", e.getMessage());
        verifyAbsentInDatabase(n);
    }

    @Test
    public void testUpdateNotification() throws Exception {
        ______TS("success: typical case");

        n = typicalNotifications.get("notification1");
        NotificationAttributes differentNotification = typicalNotifications.get("notification2");

        NotificationAttributes.UpdateOptions update1 =
                NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                        .withTitle(differentNotification.getTitle())
                        .withMessage(differentNotification.getMessage())
                        .withStyle(differentNotification.getStyle())
                        .withTargetUser(differentNotification.getTargetUser())
                        .withStartTime(differentNotification.getStartTime())
                        .withEndTime(differentNotification.getEndTime())
                        .withShown()
                        .build();

        NotificationAttributes actual = notifLogic.updateNotification(update1);
        assertEquals(differentNotification.getTitle(), actual.getTitle());
        assertEquals(differentNotification.getMessage(), actual.getMessage());
        assertEquals(differentNotification.getStyle(), actual.getStyle());
        assertEquals(differentNotification.getTargetUser(), actual.getTargetUser());
        assertEquals(differentNotification.getStartTime(), actual.getStartTime());
        assertEquals(differentNotification.getEndTime(), actual.getEndTime());
        assertTrue(actual.isShown());

        ______TS("failure: invalid update options");

        n = typicalNotifications.get("notification1");

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

        ______TS("failure: null update options");

        assertThrows(AssertionError.class, () -> notifLogic.updateNotification(null));
    }

    @Test
    public void testDeleteNotification() {
        ______TS("success: delete corresponding notification");

        n = typicalNotifications.get("notification1");
        notifLogic.deleteNotification(n.getNotificationId());

        verifyAbsentInDatabase(n);

        ______TS("failure: silent deletion of the same notification twice");

        notifLogic.deleteNotification(n.getNotificationId());

        ______TS("failure: silent deletion of non-existent notification");

        int expectedLength = notifDb.getAllNotifications().size();
        notifLogic.deleteNotification("invalid-id");
        int actualLength = notifDb.getAllNotifications().size();

        assertEquals(expectedLength, actualLength);

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class, () -> notifLogic.deleteNotification(null));
    }

    private void testGetNotification() {
        n = typicalNotifications.get("notification1");

        ______TS("success: typical case");

        NotificationAttributes actual = notifLogic.getNotification(n.getNotificationId());
        assertNotNull(actual);
        verifyNotificationEquals(n, actual);

        ______TS("failure: null parameter");

        assertThrows(AssertionError.class,
                () -> notifLogic.getNotification(null));

        ______TS("failure: non-existent notification");

        assertNull(notifLogic.getNotification("invalid_notification_id"));
    }

    private void testGetAllNotifications() {
        ______TS("success: retrieve all notifications that exist in database");

        List<NotificationAttributes> actual = notifLogic.getAllNotifications();
        assertNotNull(actual);
        typicalNotifications.values().forEach(n -> {
            assertTrue(actual.contains(n));
            actual.remove(n);
        });
    }

    private void testGetActiveNotificationsByTargetUser() {
        ______TS("success: valid target user");

        List<NotificationAttributes> actual =
                notifLogic.getActiveNotificationsByTargetUser(NotificationTargetUser.STUDENT);

        assertNotNull(actual);

        Set<NotificationAttributes> expected = new HashSet<>();
        expected.add(typicalNotifications.get("notification1"));
        expected.add(typicalNotifications.get("notification2"));
        expected.add(typicalNotifications.get("notification4"));
        expected.add(typicalNotifications.get("notification6"));

        expected.forEach(n -> {
            assertTrue(actual.contains(n));
            actual.remove(n);
        });
    }

    private void testDoesNotificationExists() {
        ______TS("case 1: true if notification exists");

        n = typicalNotifications.get("notification1");
        assertTrue(notifLogic.doesNotificationExists(n.getNotificationId()));

        ______TS("case 2: false if notification does not exist");

        assertFalse(notifLogic.doesNotificationExists("invalid-id"));
    }

    private NotificationAttributes getNewNotificationAttributes() {
        NotificationAttributes typical = typicalNotifications.get("notification1");
        return NotificationAttributes.builder(UUID.randomUUID().toString())
                .withTitle(typical.getTitle())
                .withMessage(typical.getMessage())
                .withStyle(typical.getStyle())
                .withTargetUser(typical.getTargetUser())
                .withStartTime(typical.getStartTime())
                .withEndTime(typical.getEndTime())
                .build();
    }

    private void removeNotificationsFromDb(NotificationAttributes... notifications) {
        for (NotificationAttributes notif : notifications) {
            notifDb.deleteNotification(notif.getNotificationId());
        }
    }

    private void verifyNotificationEquals(NotificationAttributes expected, NotificationAttributes actual) {
        assertEquals(expected.getNotificationId(), actual.getNotificationId());
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getStyle(), actual.getStyle());
        assertEquals(expected.getTargetUser(), actual.getTargetUser());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getStartTime(), actual.getStartTime());
        assertEquals(expected.getEndTime(), actual.getEndTime());
    }
}
