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
import teammates.storage.entity.Account;
import teammates.storage.entity.Notification;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_NOTIFICATIONS_PAGE}.
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_NOTIFICATIONS_PAGE}.
 * SUT: notification banner on {@link Const.WebPageURIs#STUDENT_HOME_PAGE}.
 */
public class NotificationsE2ETest extends BaseE2ETestCase {
    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadDataBundle("/NotificationsE2ETest.json"));
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
    }

    private void testNotificationBanner() {
        Account studentAccount = testData.accounts.get("notif.student");
        AppUrl studentHomePageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE);
        StudentHomePage homePage = loginToPage(studentHomePageUrl, StudentHomePage.class,
                studentAccount.getGoogleId());

        ______TS("verify active notification with correct information is shown");
        assertTrue(homePage.isBannerVisible(true));

        ______TS("close notification");
        // After user closes a notification banner, it should not appear till user refreshes page
        homePage.clickCloseNotificationBannerButton();
        assertFalse(homePage.isBannerVisible(false));
        homePage.reloadPage();
        assertTrue(homePage.isBannerVisible(true));

        ______TS("mark first notification as read - next unread notification should appear immediately");
        String firstNotificationId = homePage.getNotificationId();
        homePage.clickMarkAsReadButton();

        homePage.verifyStatusMessage("Notification marked as read.");
        assertTrue(homePage.isBannerVisible(true));
        String secondNotificationId = homePage.getNotificationId();
        assertNotEquals(firstNotificationId, secondNotificationId);

        ______TS("mark second notification as read - banner should disappear");
        homePage.clickMarkAsReadButton();

        homePage.verifyStatusMessage("Notification marked as read.");
        String nextNotificationId = homePage.getNotificationId();
        assertNotEquals(firstNotificationId, nextNotificationId);
        assertNotEquals(secondNotificationId, nextNotificationId);

        ______TS("verify that the notifications marked as read are reflected in the notifications page");
        AppUrl studentNotificationsPageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_NOTIFICATIONS_PAGE);
        StudentNotificationsPage notificationsPage = loginToPage(studentNotificationsPageUrl, StudentNotificationsPage.class,
                studentAccount.getGoogleId());

        Notification[] shownNotifications = {
                testData.notifications.get("notification1"),
                testData.notifications.get("notification2"),
                testData.notifications.get("notification4"),
        };

        Set<String> readNotificationIds = Stream.of(
                firstNotificationId,
                secondNotificationId,
                // notification 4 is already read when test starts
                testData.notifications.get("notification4").getId().toString()
        ).collect(Collectors.toSet());

        notificationsPage.verifyShownNotifications(shownNotifications, readNotificationIds);
    }

    @AfterClass
    public void classTeardown() {
        for (Notification notification : testData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getId());
        }
    }
}
