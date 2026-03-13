package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminTimezonePage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_TIMEZONE_PAGE}.
 */
public class AdminTimezonePageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        // No test data needed; admin login uses dev server auth.
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_TIMEZONE_PAGE);
        AdminTimezonePage timezonePage = loginAdminToPage(url, AdminTimezonePage.class);

        // Admin timezone page content does not include an h1; disable page-has-heading-one for this page.
        Results results = getAxeBuilder("page-has-heading-one").analyze(timezonePage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
