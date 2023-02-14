package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.AdminNotificationsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_NOTIFICATIONS_PAGE}.
 */
public class AdminNotificationsPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminNotificationsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_NOTIFICATIONS_PAGE);
        AdminNotificationsPage notificationsPage = loginAdminToPage(url, AdminNotificationsPage.class);

        Results results = AxeUtil.AXE_BUILDER.analyze(notificationsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
