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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * Verifies that the timezone databases in moment-timezone and java.time are consistent and up-to-date.
 *
 * <p>Implemented as a browser test as both back-end and front-end methods are involved.
 */
public class TimezoneSyncerTest extends BaseE2ETestCase {

	@Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        loginAdmin();
    }

    @BeforeMethod
    public void navigateToTimezonePage() {
        browser.driver.get(createUrl(Const.WebPageURIs.ADMIN_TIMEZONE_PAGE).toAbsoluteString());
        browser.waitForPageLoad();
    }

    @Test
    public void testFrontendBackendTimezoneDatabasesAreConsistent() {
        // ensure the front-end and the back-end have the same timezone database version
        Document pageSource = Jsoup.parse(browser.driver.getPageSource());
        String javaOffsets = processOffsets(pageSource.getElementById(Const.TestCase.TZ_JAVA).text());
        String momentOffsets = processOffsets(pageSource.getElementById(Const.TestCase.TZ_MOMENT).text());
        assertEquals(pageSource.getElementById(Const.TestCase.TZVERSION_JAVA).text(),
                pageSource.getElementById(Const.TestCase.TZVERSION_MOMENT).text());
        if (!javaOffsets.equals(momentOffsets)) {
            // Show diff when running test in Gradle
            assertEquals(Const.TestCase.START_EXPECTED + System.lineSeparator() + javaOffsets + Const.TestCase.END_EXPECTED,
            		Const.TestCase.START_ACTUAL + System.lineSeparator() + momentOffsets + Const.TestCase.END_ACTUAL);
        }
    }

    @Test
    public void testTimezoneDatabasesAreUpToDate() {
        // ensure the timezone databases are up-to-date
        String currentTzVersion = Jsoup.parse(browser.driver.getPageSource()).getElementById(Const.TestCase.TZVERSION_MOMENT).text();
        browser.driver.get(Const.TestCase.IANA_TIMEZONE_DATABASE_URL);
        Document tzReleasePage = Jsoup.parse(browser.driver.getPageSource());
        String latestTzVersion = tzReleasePage.getElementById(Const.TestCase.VERSION).text();

        if (!currentTzVersion.equals(latestTzVersion)) {
            // find the release day
            String releaseDateString = tzReleasePage.getElementById(Const.TestCase.DATE).text();
            Pattern datePattern = Pattern.compile(Const.TestCase.RELEASED);
            Matcher matcher = datePattern.matcher(releaseDateString);
            assertTrue(matcher.find());

            LocalDate releaseDate = LocalDate.parse(matcher.group(1), DateTimeFormatter.ofPattern(Const.TestCase.YYYY_MM_DD));
            LocalDate nowDate = Instant.now().atZone(Const.DEFAULT_TIME_ZONE).toLocalDate();

            assertTrue(
                    "The timezone database version is not up-to-date for more than " + Const.TestCase.DAYS_TO_UPDATE_TZ + " days,"
                            + " please update them according to the maintenance guide.",
                    releaseDate.plusDays(Const.TestCase.DAYS_TO_UPDATE_TZ).isAfter(nowDate));

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
        String[] list = offsets.split(Const.TestCase.SPACE);
        List<String> merged = new ArrayList<>();
        for (int i = 0; i < list.length; i += 2) {
            merged.add(list[i] + Const.TestCase.SPACE + list[i + 1]);
        }
        return merged.stream().collect(Collectors.joining(System.lineSeparator()));
    }

}
