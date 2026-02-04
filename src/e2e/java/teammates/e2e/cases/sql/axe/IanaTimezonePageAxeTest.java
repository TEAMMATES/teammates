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
        // No test data needed; external IANA page only.
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = new AppUrl(IANA_TIMEZONE_DATABASE_URL);
        IanaTimezonePage ianaPage = getNewPageInstance(url, IanaTimezonePage.class);

        // External IANA page; we cannot fix its markup. Disable rules it fails.
        Results results = getAxeBuilder("html-has-lang", "link-in-text-block").analyze(ianaPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
