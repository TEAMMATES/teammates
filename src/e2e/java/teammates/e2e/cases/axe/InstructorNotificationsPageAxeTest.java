package teammates.e2e.cases.axe;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorNotificationsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_NOTIFICATIONS_PAGE}.
 */
public class InstructorNotificationsPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorNotificationsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl notificationsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_NOTIFICATIONS_PAGE);
        InstructorNotificationsPage notificationsPage = loginToPage(notificationsPageUrl, InstructorNotificationsPage.class,
                testData.accounts.get("INotifs.instr").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(notificationsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

    @AfterClass
    public void classTeardown() {
        for (NotificationAttributes notification : testData.notifications.values()) {
            BACKDOOR.deleteNotification(notification.getNotificationId());
        }
    }
}
