package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.e2e.pageobjects.AdminNotificationsPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_NOTIFICATIONS_PAGE}.
 */
public class AdminNotificationsPageE2ETest extends BaseE2ETestCase {
    private NotificationAttributes[] notifications = new NotificationAttributes[2];

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminNotificationsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        notifications[0] = testData.notifications.get("notification1");
        notifications[1] = testData.notifications.get("notification2");
    }

    @Test
    @Override
    public void testAll() {

        ______TS("verify loaded data");

        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_NOTIFICATIONS_PAGE);
        AdminNotificationsPage notificationsPage = loginAdminToPage(url, AdminNotificationsPage.class);
        notificationsPage.verifyNotificationsTable(notifications);
        verifyPresentInDatabase(notifications[0]);
        verifyPresentInDatabase(notifications[1]);

        ______TS("add new notification");
        NotificationAttributes newNotification = NotificationAttributes
                .builder("tm.e2e.ANotifications.notif1")
                .withStartTime(TimeHelper.parseInstant("2035-04-01T22:00:00Z"))
                .withEndTime(TimeHelper.parseInstant("2035-04-30T20:00:00Z"))
                .withStyle(NotificationStyle.SUCCESS)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withTitle("E2E test notification 1")
                .withMessage("<p>E2E test notification message</p>")
                .build();

        notificationsPage.addNotification(newNotification);
        notificationsPage.verifyStatusMessage("Notification created successfully.");
        String newestNotificationId = notificationsPage.getNewestNotificationId();
        NotificationAttributes createdNotification = getNotification(newestNotificationId);
        notificationsPage.verifyNotificationAttributes(newNotification, createdNotification);

        ______TS("edit notification");

        ______TS("delete notification");
        notificationsPage.deleteNotification(createdNotification);
        notificationsPage.verifyStatusMessage("Notification has been deleted.");
        verifyAbsentInDatabase(createdNotification);

    }

}
