package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.AdminAccountsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_ACCOUNTS_PAGE}.
 */
public class AdminAccountsPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminAccountsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl accountsPageUrl = createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withParam(Const.ParamsNames.INSTRUCTOR_ID, "tm.e2e.AAccounts.instr2");
        AdminAccountsPage accountsPage = loginAdminToPage(accountsPageUrl, AdminAccountsPage.class);

        Results results = AxeUtil.AXE_BUILDER.analyze(accountsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
