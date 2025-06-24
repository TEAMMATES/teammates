package teammates.e2e.cases.sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminNotificationsPage;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_NOTIFICATIONS_PAGE}.
 */
public class AdminNotificationsPageE2ETest extends BaseE2ETestCase {
    private Notification[] notifications = new Notification[2];

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/AdminNotificationsPageE2ETestSql.json"));
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
        NotificationData notif = getNotification(notifications[0].getId().toString());
        assertEquals(notif.getNotificationId(), notifications[0].getId().toString());
        assertEquals(notif.getMessage(), notifications[0].getMessage());
        assertEquals(notif.getTitle(), notifications[0].getTitle());
        notif = getNotification(notifications[1].getId().toString());
        assertEquals(notif.getNotificationId(), notifications[1].getId().toString());
        assertEquals(notif.getMessage(), notifications[1].getMessage());
        assertEquals(notif.getTitle(), notifications[1].getTitle());

        ______TS("add new notification");
        int currentYear = LocalDate.now().getYear();
        Notification newNotification = new Notification(
                LocalDateTime.of(currentYear + 5, 2, 2, 12, 0).atZone(ZoneId.of("UTC")).toInstant(),
                LocalDateTime.of(currentYear + 5, 2, 3, 12, 0).atZone(ZoneId.of("UTC")).toInstant(),
                NotificationStyle.INFO,
                NotificationTargetUser.STUDENT,
                "New E2E test notification 1",
                "<p>New E2E test notification message</p>"
                );

        notificationsPage.addNotification(newNotification);
        notificationsPage.verifyStatusMessage("Notification created successfully.");

        // Replace placeholder ID with actual ID of created notification
        notificationsPage.sortNotificationsTableByDescendingCreateTime();
        String newestNotificationId = notificationsPage.getFirstRowNotificationId();
        newNotification.setId(UUID.fromString(newestNotificationId));

        // Checks that notification is in the database first
        // so that newNotification is updated with the created time before checking table row
        notif = getNotification(newestNotificationId);
        assertEquals(notif.getNotificationId(), newestNotificationId);
        assertEquals(notif.getMessage(), newNotification.getMessage());
        assertEquals(notif.getTitle(), newNotification.getTitle());
        notificationsPage.verifyNotificationsTableRow(newNotification);

        ______TS("edit notification");
        newNotification.setStartTime(LocalDateTime.of(currentYear + 7, 2, 2, 12, 0).atZone(ZoneId.of("UTC")).toInstant());
        newNotification.setEndTime(LocalDateTime.of(currentYear + 7, 2, 3, 12, 0).atZone(ZoneId.of("UTC")).toInstant());
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
        notif = getNotification(newestNotificationId);
        assertNull(notif);
    }

    @AfterClass
    public void classTeardown() {
        for (Notification notification : testData.notifications.values()) {
            deleteNotification(notification.getId().toString());
        }
    }
}
