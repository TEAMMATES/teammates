package teammates.test.cases.browsertests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * Verifies that the timezone databases in momentjs and java.time are consistent and up-to-date.
 *
 * <p>Implemented as a browser test as both back-end and front-end methods are involved.
 */
public class TimezoneSyncerTest extends BaseUiTestCase {

    private static final String IANA_TIMEZONE_DATABASE_URL = "https://www.iana.org/time-zones";

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        loginAdmin();
    }

    @Test
    public void testFrontendBackendTimezoneDatabasesAreConsistent() {
        // ensure the front-end and the back-end have the same timezone database version
        browser.driver.get(createUrl(Const.ViewURIs.TIMEZONE).toAbsoluteString());
        Document pageSource = Jsoup.parse(browser.driver.getPageSource());
        assertEquals(pageSource.getElementById("javatime").text().replace(" ", System.lineSeparator()),
                     pageSource.getElementById("momentjs").text().replace(" ", System.lineSeparator()));
    }

    @Test
    public void testTimezoneDatabasesAreUpToDate() {
        // ensure the timezone databases are up-to-date
        browser.driver.get(createUrl(Const.ViewURIs.TIMEZONE).toAbsoluteString());
        String currentTzVersion = Jsoup.parse(browser.driver.getPageSource()).getElementById("version").text();
        browser.driver.get(IANA_TIMEZONE_DATABASE_URL);
        String latestTzVersion = Jsoup.parse(browser.driver.getPageSource()).getElementById("version").text();
        assertEquals(
                "The timezone database version is not up-to-date, please update them according to the maintenance guide.",
                latestTzVersion, currentTzVersion);
    }

}
