package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.e2e.pageobjects.HomePage;

/**
 * SUT: TEAMMATES front page (public landing page).
 */
public class HomePageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        // No test data needed; public landing page only.
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl("/web/front");
        HomePage homePage = getNewPageInstance(url, HomePage.class);

        Results results = getAxeBuilder().analyze(homePage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
