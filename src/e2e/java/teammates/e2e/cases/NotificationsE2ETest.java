package teammates.e2e.cases;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorNotificationsPage;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.e2e.pageobjects.StudentNotificationsPage;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.AccountData;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_NOTIFICATIONS_PAGE}.
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_NOTIFICATIONS_PAGE}.
 * SUT: notification banner on {@link Const.WebPageURIs#STUDENT_HOME_PAGE}.
 */
public class NotificationsE2ETest extends BaseE2ETestCase {
    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadDataBundle("/NotificationsE2ETestSql.json"));
    }

    @Test
    @Override
    protected void testAll() {
        testInstructor();
        testStudent();
        testNotificationBanner();
    }

    private void testInstructor() {
        Account instructorAccount = testData.accounts.get("notif.instructor");
        AppUrl instructorNotificationsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_NOTIFICATIONS_PAGE);
        InstructorNotificationsPage notificationsPage = loginToPage(instructorNotificationsPageUrl,
                InstructorNotificationsPage.class, instructorAccount.getGoogleId());

        ______TS("verify that only active notifications for instructors are shown");
        Notification[] notShownNotifications = {
                testData.notifications.get("notification2"),
                testData.notifications.get("expiredNotification1"),
        };
        Notification[] shownNotifications = {
                testData.notifications.get("notification1"),
                testData.notifications.get("notification3"),
                testData.notifications.get("notification4"),
        };

        Notification[] readNotifications = {
                testData.notifications.get("notification4"),
        };

        Set<String> readNotificationsIds = Stream.of(readNotifications)
                .map(readNotification -> readNotification.getId().toString())
                .collect(Collectors.toSet());

        notificationsPage.verifyNotShownNotifications(notShownNotifications);
        notificationsPage.verifyShownNotifications(shownNotifications, readNotificationsIds);

        ______TS("mark all notifications as read");
        notificationsPage.markAllNotificationsAsRead();
        notificationsPage.verifyStatusMessage("All notifications marked as read!");

        teammates.test.ThreadHelper.waitFor(2000);

        ______TS("notification banner is not visible");
        assertFalse(notificationsPage.isBannerVisible());

        AccountData updatedAccountFromDb = BACKDOOR.getAccountData(instructorAccount.getGoogleId());
        Notification notification1 = testData.notifications.get("notification1");
        Notification notification3 = testData.notifications.get("notification3");

        assertTrue(updatedAccountFromDb.getReadNotifications().containsKey(notification1.getId().toString()));
        assertTrue(updatedAccountFromDb.getReadNotifications().containsKey(notification3.getId().toString()));
    }

    private void testStudent() {
        Account studentAccount = testData.accounts.get("notif.student");
        AppUrl studentNotificationsPageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_NOTIFICATIONS_PAGE);
        StudentNotificationsPage notificationsPage = loginToPage(studentNotificationsPageUrl, StudentNotificationsPage.class,
                studentAccount.getGoogleId());

        ______TS("verify that only active notifications for students are shown");
        Notification[] notShownNotifications = {
                testData.notifications.get("notification3"),
                testData.notifications.get("expiredNotification1"),
        };

        Notification[] shownNotifications = {
                testData.notifications.get("notification1"),
                testData.notifications.get("notification2"),
                testData.notifications.get("notification4"),
        };

        Notification[] readNotifications = {
                testData.notifications.get("notification4"),
        };

        Set<String> readNotificationsIds = Stream.of(readNotifications)
                .map(readNotification -> readNotification.getId().toString())
                .collect(Collectors.toSet());

        notificationsPage.verifyNotShownNotifications(notShownNotifications);
        notificationsPage.verifyShownNotifications(shownNotifications, readNotificationsIds);

        ______TS("mark all notifications as read");
        notificationsPage.markAllNotificationsAsRead();
        notificationsPage.verifyStatusMessage("All notifications marked as read!");

        teammates.test.ThreadHelper.waitFor(2000);

        ______TS("notification banner is not visible");
        assertFalse(notificationsPage.isBannerVisible());

        AccountData updatedAccountFromDb = BACKDOOR.getAccountData(studentAccount.getGoogleId());
        Notification notification1 = testData.notifications.get("notification1");
        Notification notification2 = testData.notifications.get("notification2");

        assertTrue(updatedAccountFromDb.getReadNotifications().containsKey(notification1.getId().toString()));
        assertTrue(updatedAccountFromDb.getReadNotifications().containsKey(notification2.getId().toString()));
    }

    private void testNotificationBanner() {
        Account studentAccount = testData.accounts.get("notif.studentBanner");
        AppUrl studentHomePageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE);
        StudentHomePage homePage = loginToPage(studentHomePageUrl, StudentHomePage.class,
                studentAccount.getGoogleId());

        ______TS("verify active notification with correct information is shown");
        assertTrue(homePage.isBannerVisible());

        ______TS("close notification");
        // After user closes a notification banner, it should not appear till user refreshes page
        homePage.clickCloseNotificationBannerButton();
        assertFalse(homePage.isBannerVisible());
        homePage.reloadPage();
        assertTrue(homePage.isBannerVisible());

        ______TS("mark first notification as read - next unread notification should appear immediately");
        String firstNotificationId = homePage.getNotificationId();
        homePage.clickMarkAsReadButton();

        homePage.verifyStatusMessage("Notification marked as read.");
        assertTrue(homePage.isBannerVisible());
        String secondNotificationId = homePage.getNotificationId();
        assertNotEquals(firstNotificationId, secondNotificationId);

        ______TS("mark second notification as read - banner should disappear");
        homePage.clickMarkAsReadButton();

        homePage.verifyStatusMessage("Notification marked as read.");
        if (homePage.isBannerVisible()) {
            String nextNotificationId = homePage.getNotificationId();
            assertNotEquals(firstNotificationId, nextNotificationId);
            assertNotEquals(secondNotificationId, nextNotificationId);
        } else {
            assertFalse(homePage.isBannerVisible());
        }
        AccountData accountFromDb = BACKDOOR.getAccountData(studentAccount.getGoogleId());
        assertTrue(accountFromDb.getReadNotifications().containsKey(firstNotificationId));
        assertTrue(accountFromDb.getReadNotifications().containsKey(secondNotificationId));
    }

    @AfterClass
    public void classTeardown() {
        for (Notification notification : testData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getId());
        }
    }
}
