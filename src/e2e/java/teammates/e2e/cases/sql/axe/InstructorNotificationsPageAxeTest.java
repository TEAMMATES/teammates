package teammates.e2e.cases.sql.axe;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorNotificationsPage;
import teammates.storage.sqlentity.Notification;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_NOTIFICATIONS_PAGE}.
 */
public class InstructorNotificationsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorNotificationsPageE2ESqlTest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl notificationsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_NOTIFICATIONS_PAGE);
        InstructorNotificationsPage notificationsPage = loginToPage(notificationsPageUrl, InstructorNotificationsPage.class,
                testData.accounts.get("INotifs.instr").getGoogleId());

        Results results = getAxeBuilder().analyze(notificationsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

    @AfterClass
    public void classTeardown() {
        for (Notification notification : testData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getId());
        }
    }
}
