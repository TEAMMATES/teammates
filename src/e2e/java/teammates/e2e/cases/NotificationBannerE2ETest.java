package teammates.e2e.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.storage.sqlentity.Account;
import teammates.ui.output.AccountData;

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
        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/NotificationBannerE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    protected void testAll() {
        Account account = sqlTestData.accounts.get("NotifBanner.student");
        AppUrl studentHomePageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE);
        StudentHomePage studentHomePage = loginToPage(studentHomePageUrl, StudentHomePage.class,
                account.getGoogleId());

        ______TS("verify active notification with correct information is shown");
        assertTrue(studentHomePage.isBannerVisible());

        ______TS("close notification");
        // After user closes a notification banner, it should not appear till user refreshes page
        studentHomePage.clickCloseNotificationBannerButton();
        assertFalse(studentHomePage.isBannerVisible());
        studentHomePage.reloadPage();
        assertTrue(studentHomePage.isBannerVisible());

        ______TS("mark notification as read");
        studentHomePage.reloadPage();
        assertTrue(studentHomePage.isBannerVisible());

        String notificationId = studentHomePage.getNotificationId();
        studentHomePage.clickMarkAsReadButton();
        AccountData accountFromDb = BACKDOOR.getAccountData(account.getGoogleId());

        studentHomePage.verifyStatusMessage("Notification marked as read.");
        assertFalse(studentHomePage.isBannerVisible());
        assertTrue(accountFromDb.getReadNotifications().containsKey(notificationId));

    }

    @AfterClass
    public void classTeardown() {
        for (NotificationAttributes notification : testData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getNotificationId());
        }
    }
}
