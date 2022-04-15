package teammates.e2e.cases;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.PageWithNotificationBanner;

/**
 * SUT: Pages that can display notification banners.
 * Used in this test and arbitrarily chosen {@link Const.WebPageURIs#STUDENT_HOME_PAGE},
 * {@link Const.WebPageURIs#STUDENT_PROFILE_PAGE}, {@link Const.WebPageURIs#STUDENT_NOTIFICATIONS_PAGE}
 */
public class NotificationBannerE2ETest extends BaseE2ETestCase {
    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/NotificationBannerE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    protected void testAll() {
        AccountAttributes account = testData.accounts.get("NotifBanner.student");
        NotificationAttributes notification = testData.notifications.get("notification1");
        AppUrl studentHomePageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE);
        PageWithNotificationBanner page = loginToPage(studentHomePageUrl, PageWithNotificationBanner.class,
                account.getGoogleId());

        ______TS("verify that active notifications with correct information are shown");
        page.verifyNotificationBannerIsVisible(notification);

        ______TS("close notification");
        // After user closes a notification banner, it should not appear till user refreshes page
        page.clickCloseNotificationBannerButton();
        page.verifyNotificationBannerIsNotVisible();
        page.clickHelpPageNavLink();
        page.verifyNotificationBannerIsNotVisible();
        page.reloadPage();
        page.verifyNotificationBannerIsVisible(notification);

        ______TS("navigating to notifications page closes notification banner");
        page.clickHomePageNavLink();
        page.reloadPage();
        page.verifyNotificationBannerIsVisible(notification);

        // After user visits notification page, it should not appear till user refreshes page
        page.clickNotificationPageNavLink();
        page.verifyNotificationBannerIsNotVisible();
        page.clickHomePageNavLink();
        page.verifyNotificationBannerIsNotVisible();
        page.reloadPage();
        page.verifyNotificationBannerIsVisible(notification);

        ______TS("mark notification as read");
        page.clickHomePageNavLink();
        page.reloadPage();
        page.verifyNotificationBannerIsVisible(notification);
        page.clickMarkAsReadButton();
        page.verifyStatusMessage("Notification marked as read.");
        page.verifyNotificationBannerIsNotVisible();

        Map<String, Instant> readNotifications = new HashMap<>();
        readNotifications.put(notification.getNotificationId(), notification.getEndTime());

        account.setReadNotifications(readNotifications);
        verifyPresentInDatabase(account);

        ______TS("delete test notifications from database");
        for (NotificationAttributes n : testData.notifications.values()) {
            BACKDOOR.deleteNotification(n.getNotificationId());
            verifyAbsentInDatabase(n);
        }
    }
}
