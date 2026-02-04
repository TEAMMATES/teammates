package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.e2e.pageobjects.IanaTimezonePage;

/**
 * SUT: IANA time zone database page (external).
 */
public class IanaTimezonePageAxeTest extends BaseAxeTestCase {

    private static final String IANA_TIMEZONE_DATABASE_URL = "https://www.iana.org/time-zones";

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/AdminHomePageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = new AppUrl(IANA_TIMEZONE_DATABASE_URL);
        IanaTimezonePage ianaPage = getNewPageInstance(url, IanaTimezonePage.class);

        Results results = getAxeBuilder().analyze(ianaPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
