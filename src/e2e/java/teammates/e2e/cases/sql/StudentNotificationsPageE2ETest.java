package teammates.e2e.cases.sql;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentNotificationsPage;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.ui.output.AccountData;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_NOTIFICATIONS_PAGE}.
 */
public class StudentNotificationsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/StudentNotificationsPageE2ETestSql.json");
        testData = removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        Account account = testData.accounts.get("SNotifs.student");
        AppUrl notificationsPageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_NOTIFICATIONS_PAGE);
        StudentNotificationsPage notificationsPage = loginToPage(notificationsPageUrl, StudentNotificationsPage.class,
                account.getGoogleId());

        ______TS("verify that only active notifications with correct target user are shown");
        Notification[] notShownNotifications = {
                testData.notifications.get("notification3"),
                testData.notifications.get("expiredNotification1"),
        };

        Notification[] shownNotifications = {
                testData.notifications.get("notification1"),
                testData.notifications.get("notification2"),
                testData.notifications.get("notification4"),
        };

        ReadNotification[] readNotifications = testData.readNotifications.values().toArray(ReadNotification[]::new);

        Set<String> readNotificationsIds = Stream.of(readNotifications)
                .map(readNotification -> readNotification.getNotification().getId().toString())
                .collect(Collectors.toSet());

        notificationsPage.verifyNotShownNotifications(notShownNotifications);
        notificationsPage.verifyShownNotifications(shownNotifications, readNotificationsIds);

        ______TS("mark notification as read");
        Notification notificationToMarkAsRead = testData.notifications.get("notification2");
        notificationsPage.markNotificationAsRead(notificationToMarkAsRead);
        notificationsPage.verifyStatusMessage("Notification marked as read.");

        // Verify that account's readNotifications attribute is updated
        AccountData accountFromDb = BACKDOOR.getAccountData(account.getGoogleId());
        assertTrue(accountFromDb.getReadNotifications().containsKey(notificationToMarkAsRead.getId().toString()));

        ______TS("notification banner is not visible");
        assertFalse(notificationsPage.isBannerVisible());
    }

    @AfterClass
    public void classTeardown() {
        for (Notification notification : testData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getId());
        }
    }
}
