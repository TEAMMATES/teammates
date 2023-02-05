package teammates.e2e.cases;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;

/**
 * SUT: The reusable notification banner, which can be displayed across many pages.
 * {@link Const.WebPageURIs#STUDENT_HOME_PAGE} is used to test the behaviour of the banner in this case,
 * {@link Const.WebPageURIs#STUDENT_NOTIFICATIONS_PAGE}
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
        StudentHomePage studentHomePage = loginToPage(studentHomePageUrl, StudentHomePage.class,
                account.getGoogleId());

        ______TS("verify active notification with correct information is shown");
        assertTrue(studentHomePage.isBannerVisible());
        studentHomePage.verifyBannerContent(notification);

        ______TS("close notification");
        // After user closes a notification banner, it should not appear till user refreshes page
        studentHomePage.clickCloseNotificationBannerButton();
        assertFalse(studentHomePage.isBannerVisible());
        studentHomePage.reloadPage();
        assertTrue(studentHomePage.isBannerVisible());

        ______TS("mark notification as read");
        studentHomePage.reloadPage();
        assertTrue(studentHomePage.isBannerVisible());

        studentHomePage.clickMarkAsReadButton();
        studentHomePage.verifyStatusMessage("Notification marked as read.");
        assertFalse(studentHomePage.isBannerVisible());

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
