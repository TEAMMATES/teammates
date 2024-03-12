package teammates.e2e.cases.axe;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentNotificationsPage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_NOTIFICATIONS_PAGE}.
 */
public class StudentNotificationsPageAxeTest extends BaseAxeTestCase {

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
        AppUrl notificationsPageUrl = createFrontendUrl(Const.WebPageURIs.STUDENT_NOTIFICATIONS_PAGE);
        StudentNotificationsPage notificationsPage = loginToPage(notificationsPageUrl, StudentNotificationsPage.class,
                sqlTestData.accounts.get("SNotifs.student").getGoogleId());

        Results results = getAxeBuilder().analyze(notificationsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

    @AfterClass
    public void classTeardown() {
        for (NotificationAttributes notification : testData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getNotificationId());
        }
    }
}
