package teammates.e2e.cases.e2e;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * Verifies that the timezone databases in moment-timezone and java.time are consistent and up-to-date.
 *
 * <p>Implemented as a browser test as both back-end and front-end methods are involved.
 */
public class TimezoneSyncerTest extends BaseE2ETestCase {

    private static final String IANA_TIMEZONE_DATABASE_URL = "https://www.iana.org/time-zones";
    private static final int DAYS_TO_UPDATE_TZ = 120;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        loginAdmin();
    }

    @Test
    @Override
    public void testAll() {
        testFrontendBackendTimezoneDatabasesAreConsistent();
        testTimezoneDatabasesAreUpToDate();

    }

    private void testFrontendBackendTimezoneDatabasesAreConsistent() {
        browser.driver.get(createUrl(Const.WebPageURIs.ADMIN_TIMEZONE_PAGE).toAbsoluteString());
        browser.waitForPageLoad();

        // ensure the front-end and the back-end have the same timezone database version
        Document pageSource = Jsoup.parse(browser.driver.getPageSource());
        String javaOffsets = processOffsets(pageSource.getElementById("tz-java").text());
        String momentOffsets = processOffsets(pageSource.getElementById("tz-moment").text());
        assertEquals(pageSource.getElementById("tzversion-java").text(),
                pageSource.getElementById("tzversion-moment").text());
        if (!javaOffsets.equals(momentOffsets)) {
            // Show diff when running test in Gradle
            assertEquals("<expected>" + System.lineSeparator() + javaOffsets + "</expected>",
                    "<actual>" + System.lineSeparator() + momentOffsets + "</actual>");
        }
    }

    private void testTimezoneDatabasesAreUpToDate() {
        // ensure the timezone databases are up-to-date
        String currentTzVersion = Jsoup.parse(browser.driver.getPageSource()).getElementById("tzversion-moment").text();
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
                    "The timezone database version is not up-to-date for more than " + DAYS_TO_UPDATE_TZ + " days,"
                            + " please update them according to the maintenance guide.",
                    releaseDate.plusDays(DAYS_TO_UPDATE_TZ).isAfter(nowDate));

        }

        logout();
    }

    private String processOffsets(String offsets) {
        // This will process raw offset strings, e.g. Zone1 1 Zone2 -2 Zone3 3 ... to:
        // Zone1 1
        // Zone2 -2
        // Zone3 3
        // ...
        // to facilitate easy diff-ing when the need arises
        String[] list = offsets.split(" ");
        List<String> merged = new ArrayList<>();
        for (int i = 0; i < list.length; i += 2) {
            merged.add(list[i] + " " + list[i + 1]);
        }
        return merged.stream().collect(Collectors.joining(System.lineSeparator()));
    }

}
