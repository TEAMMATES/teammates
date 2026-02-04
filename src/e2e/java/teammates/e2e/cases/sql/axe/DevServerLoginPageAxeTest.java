package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.DevServerLoginPage;

/**
 * SUT: Dev server login page (shown when accessing a protected page without auth).
 */
public class DevServerLoginPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/AdminHomePageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        // Navigate to a protected page to get redirected to the dev server login page.
        AppUrl protectedUrl = createFrontendUrl(Const.WebPageURIs.ADMIN_HOME_PAGE);
        DevServerLoginPage loginPage = getNewPageInstance(protectedUrl, DevServerLoginPage.class);

        Results results = getAxeBuilder().analyze(loginPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
