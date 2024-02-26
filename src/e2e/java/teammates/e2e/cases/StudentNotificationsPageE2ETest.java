package teammates.e2e.cases;

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
import teammates.ui.output.AccountData;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_NOTIFICATIONS_PAGE}.
 */
public class StudentNotificationsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentNotificationsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData = removeAndRestoreSqlDataBundle(
                    loadSqlDataBundle("/StudentNotificationsPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        Account account = sqlTestData.accounts.get("SNotifs.student");
        AppUrl notificationsPageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_NOTIFICATIONS_PAGE);
        StudentNotificationsPage notificationsPage = loginToPage(notificationsPageUrl, StudentNotificationsPage.class,
                account.getGoogleId());

        ______TS("verify that only active notifications with correct target user are shown");
        Notification[] notShownNotifications = {
                sqlTestData.notifications.get("notification3"),
                sqlTestData.notifications.get("expiredNotification1"),
        };
        Notification[] shownNotifications = {
                sqlTestData.notifications.get("notification1"),
                sqlTestData.notifications.get("notification2"),
                sqlTestData.notifications.get("notification4"),
        };

        Notification[] readNotifications = {
                sqlTestData.notifications.get("notification4"),
        };

        Set<String> readNotificationsIds = Stream.of(readNotifications)
                .map(readNotification -> readNotification.getId().toString())
                .collect(Collectors.toSet());

        notificationsPage.verifyNotShownNotifications(notShownNotifications);
        notificationsPage.verifyShownNotifications(shownNotifications, readNotificationsIds);

        ______TS("mark notification as read");
        Notification notificationToMarkAsRead = sqlTestData.notifications.get("notification2");
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
        for (Notification notification : sqlTestData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getId());
        }
    }

}
