package teammates.e2e.cases;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminTimezonePage;
import teammates.e2e.pageobjects.IanaTimezonePage;

/**
 * Verifies that the timezone databases in moment-timezone and java.time are consistent and up-to-date.
 *
 * <p>Implemented as a browser test as both back-end and front-end methods are involved.
 */
public class TimezoneSyncerTest extends BaseE2ETestCase {

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
        AdminTimezonePage timezonePage = getNewPageInstance(
                createUrl(Const.WebPageURIs.ADMIN_TIMEZONE_PAGE), AdminTimezonePage.class);

        ______TS("ensure the front-end and the back-end have the same timezone database version");
        String javaOffsets = timezonePage.getJavaTimezoneOffsets();
        String momentOffsets = timezonePage.getMomentTimezoneOffsets();
        assertEquals(timezonePage.getJavaTimezoneVersion(), timezonePage.getMomentTimezoneVersion());
        if (!javaOffsets.equals(momentOffsets)) {
            // Show diff when running test in Gradle
            assertEquals("<expected>" + System.lineSeparator() + javaOffsets + "</expected>",
                    "<actual>" + System.lineSeparator() + momentOffsets + "</actual>");
        }

        ______TS("ensure the timezone databases are up-to-date");
        String currentTzVersion = timezonePage.getMomentTimezoneVersion();
        IanaTimezonePage ianaPage = getNewPageInstance(
                new AppUrl(IanaTimezonePage.IANA_TIMEZONE_DATABASE_URL), IanaTimezonePage.class);
        ianaPage.waitForPageToLoad();
        String latestTzVersion = ianaPage.getVersion();

        if (!currentTzVersion.equals(latestTzVersion)) {
            // find the release day
            String releaseDateString = ianaPage.getReleaseDate();
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
    }

}
