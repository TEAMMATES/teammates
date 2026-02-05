package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminNotificationsPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_NOTIFICATIONS_PAGE}.
 */
public class AdminNotificationsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/AdminNotificationsPageE2ETestSql.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_NOTIFICATIONS_PAGE);
        AdminNotificationsPage notificationsPage = loginAdminToPage(url, AdminNotificationsPage.class);

        Results results = getAxeBuilder().analyze(notificationsPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
