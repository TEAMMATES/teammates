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
        // Only validates that the notifications are present in the notifications table instead of checking every row
        // This is because the page will display all notifications in the database, which is not predictable
        notificationsPage.verifyNotificationsTableRow(notifications[0]);
        notificationsPage.verifyNotificationsTableRow(notifications[1]);
        verifyPresentInDatabase(notifications[0]);
        verifyPresentInDatabase(notifications[1]);

        ______TS("add new notification");
        NotificationAttributes newNotification = NotificationAttributes
                .builder("placeholder-notif-id")
                .withStartTime(TimeHelper.parseInstant("2030-04-01T22:00:00Z"))
                .withEndTime(TimeHelper.parseInstant("2030-04-30T20:00:00Z"))
                .withStyle(NotificationStyle.SUCCESS)
                .withTargetUser(NotificationTargetUser.GENERAL)
                .withTitle("E2E test notification 1")
                .withMessage("<p>E2E test notification message</p>")
                .build();

        notificationsPage.addNotification(newNotification);
        notificationsPage.verifyStatusMessage("Notification created successfully.");

        // Replace placeholder ID with actual ID of created notification
        notificationsPage.sortNotificationsTableByDescendingCreateTime();
        String newestNotificationId = notificationsPage.getFirstRowNotificationId();
        newNotification.setNotificationId(newestNotificationId);

        // Checks that notification is in the database first
        // so that newNotification is updated with the created time before checking table row
        verifyPresentInDatabase(newNotification);
        notificationsPage.verifyNotificationsTableRow(newNotification);

        ______TS("edit notification");
        newNotification.setStartTime(TimeHelper.parseInstant("2025-05-09T12:00:00Z"));
        newNotification.setEndTime(TimeHelper.parseInstant("2030-06-01T22:00:00Z"));
        newNotification.setStyle(NotificationStyle.DANGER);
        newNotification.setTargetUser(NotificationTargetUser.INSTRUCTOR);
        newNotification.setTitle("Edited E2E test notification 1");
        newNotification.setMessage("<p>Edited E2E test notification message</p>");

        notificationsPage.editNotification(newNotification);
        notificationsPage.verifyStatusMessage("Notification updated successfully.");
        notificationsPage.verifyNotificationsTableRow(newNotification);

        // verify that notification is present in database by reloading
        notificationsPage.reloadPage();
        notificationsPage.verifyNotificationsTableRow(newNotification);

        ______TS("delete notification");
        notificationsPage.deleteNotification(newNotification);
        notificationsPage.verifyStatusMessage("Notification has been deleted.");
        verifyAbsentInDatabase(newNotification);

        ______TS("delete test notifications from database");
        for (NotificationAttributes notification : notifications) {
            notificationsPage.deleteNotification(notification);
            verifyAbsentInDatabase(notification);
        }

    }

}
