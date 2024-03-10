package teammates.e2e.cases.axe;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorNotificationsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_NOTIFICATIONS_PAGE}.
 */
public class InstructorNotificationsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorNotificationsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorNotificationsPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl notificationsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_NOTIFICATIONS_PAGE);
        InstructorNotificationsPage notificationsPage = loginToPage(notificationsPageUrl, InstructorNotificationsPage.class,
                sqlTestData.accounts.get("INotifs.instr").getGoogleId());

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
