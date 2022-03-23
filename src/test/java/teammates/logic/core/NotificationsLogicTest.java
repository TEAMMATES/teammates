package teammates.logic.core;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.common.datatransfer.attributes.NotificationAttributes;
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
        testCreateNotification();
    }

    private void testGetNotification() throws Exception {
        ______TS("success: typical case");

        n = getTypicalNotification();
        notifDb.createEntity(n);

        NotificationAttributes retrievedNotif = notifLogic.getNotification(n.getNotificationId());
        assertEqual(n, retrievedNotif);

        notifDb.deleteNotification(n.getNotificationId());
        ______TS("failure: Null parameter");

        assertThrows(AssertionError.class,
                () -> notifLogic.getNotification(null));

        ______TS("failure: notification doesn't exist");

        assertNull(notifLogic.getNotification("nonexistant-notification"));
    }

    private void testCreateNotification() throws Exception {
        ______TS("success: typical case");

        n = getTypicalNotification();
        notifLogic.createNotification(n);
        // Equivalent to verifyInDatabase, since there is no getNotification endpoint
        assertEquals(n, notifDb.getNotification(n.getNotificationId()));

        notifDb.deleteNotification(n.getNotificationId());
        ______TS("failure: Null parameter");

        assertThrows(AssertionError.class,
                () -> notifLogic.createNotification(null));

        ______TS("failure: test create with invalid title name");

        n = getTypicalNotification();
        n.setTitle("");
        Exception e = assertThrows(Exception.class, () -> notifLogic.createNotification(n));
        assertEquals("The field 'notification title' is empty.", e.getMessage());
        // Equivalent to verifyAbsentInDatabase, since there is no getNotification endpoint
        assertNull(notifDb.getNotification(n.getNotificationId()));
    }

    private NotificationAttributes getTypicalNotification() {
        return NotificationAttributes
                .builder("some-notif-id")
                .withStartTime(Instant.now())
                .withEndTime(Instant.now().plusSeconds(100))
                .withType(NotificationType.MAINTENANCE)
                .withTargetUser(NotificationTargetUser.INSTRUCTOR)
                .withTitle("Maintenance Notice")
                .withMessage("Maintenance at 3pm today")
                .build();
    }

    // Equivalent to verifyInDatabase
    // we are unable to implement the methods necessary in BaseTestCaseWithDatabaseAccess to support this
    // due to a dependency in BaseE2ETest that requires a getNotification endpoint which
    // our API does not support
    private void assertEqual(NotificationAttributes notif1, NotificationAttributes notif2) {
        assertEquals(notif1.getNotificationId(), notif2.getNotificationId());
        assertEquals(notif1.getStartTime(), notif2.getStartTime());
        assertEquals(notif1.getEndTime(), notif2.getEndTime());
        assertEquals(notif1.getType(), notif2.getType());
        assertEquals(notif1.getTargetUser(), notif2.getTargetUser());
        assertEquals(notif1.getTitle(), notif2.getTitle());
        assertEquals(notif1.getMessage(), notif2.getMessage());
    }
}
