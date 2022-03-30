package teammates.logic.core;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
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
        verifyPresentInDatabase(retrievedNotif);

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
        verifyPresentInDatabase(n);

        notifDb.deleteNotification(n.getNotificationId());
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
}
