package teammates.e2e.cases;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private static final String IANA_TIMEZONE_DATABASE_VERSION_URL = "https://data.iana.org/time-zones/tzdb/version";

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @Test
    @Override
    public void testAll() {
        AdminTimezonePage timezonePage = loginAdminToPage(
                createFrontendUrl(Const.WebPageURIs.ADMIN_TIMEZONE_PAGE), AdminTimezonePage.class);

        ______TS("ensure the front-end and the back-end have the same timezone database version");
        String javaOffsets = timezonePage.getJavaTimezoneOffsets();
        String momentOffsets = timezonePage.getMomentTimezoneOffsets();
        assertEquals(
                timezonePage.getJavaTimezoneVersion(),
                timezonePage.getMomentTimezoneVersion(),
                "The timezone database versions are not in sync. For information on updating the timezone databases, "
                + "see the maintainer guide in the TEAMMATES ops repository."
        );
        if (!javaOffsets.equals(momentOffsets)) {
            // Show diff when running test in Gradle
            assertEquals("<expected>" + System.lineSeparator() + javaOffsets + "</expected>",
                    "<actual>" + System.lineSeparator() + momentOffsets + "</actual>");
        }

        ______TS("ensure the timezone databases are up-to-date");
        String currentTzVersion = timezonePage.getMomentTimezoneVersion();
        IanaTimezonePage ianaPage = getNewPageInstance(
                new AppUrl(IANA_TIMEZONE_DATABASE_VERSION_URL), IanaTimezonePage.class);
        String latestTzVersion = ianaPage.getVersion();

        assertEquals(latestTzVersion, currentTzVersion,
                "The timezone database version is not up-to-date, "
                        + "please update them according to the maintenance guide.");
    }

}
