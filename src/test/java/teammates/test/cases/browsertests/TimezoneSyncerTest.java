package teammates.test.cases.browsertests;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.e2e.cases.e2e.BaseE2ETestCase;

/**
 * Verifies that the timezone databases in momentjs and java.time are consistent and up-to-date.
 *
 * <p>Implemented as a browser test as both back-end and front-end methods are involved.
 */
public class TimezoneSyncerTest extends BaseE2ETestCase {

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
        Document tzReleasePage = Jsoup.parse(browser.driver.getPageSource());
        String latestTzVersion = tzReleasePage.getElementById("version").text();

        if (!currentTzVersion.equals(latestTzVersion)) {
            // find the release day
            String releaseDateString = tzReleasePage.getElementById("date").text();
            Pattern datePattern = Pattern.compile("\\(Released (.+)\\)");
            Matcher matcher = datePattern.matcher(releaseDateString);
            assertTrue(matcher.find());

            LocalDate releaseDate = LocalDate.parse(matcher.group(1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate nowDate = Instant.now().atZone(Const.DEFAULT_TIME_ZONE).toLocalDate();

            assertTrue(
                    "The timezone database version is not up-to-date for more than 20 days,"
                            + " please update them according to the maintenance guide.",
                    releaseDate.plusDays(20).isAfter(nowDate));

        }
    }

}
