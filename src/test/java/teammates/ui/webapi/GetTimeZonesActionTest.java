package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.TimeZonesData;

/**
 * SUT: {@link GetTimeZonesAction}.
 */
public class GetTimeZonesActionTest extends BaseActionTest<GetTimeZonesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.TIMEZONE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        ______TS("Normal case");

        GetTimeZonesAction a = getAction();
        JsonResult r = getJsonResult(a);

        TimeZonesData output = (TimeZonesData) r.getOutput();

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        // This test does not check the timezone database used is the latest
        // Only check that the version number is returned, and some sample values for timezone offset
        assertNotNull(output.getVersion());

        // There is a quirk in the ETC/GMT time zones due to the tzdb using POSIX-style signs in the zone names and the
        // output abbreviations. POSIX has positive signs west of Greenwich, while we are used to positive signs east
        // of Greenwich in practice. For example, TZ='Etc/GMT+8' uses the abbreviation "GMT+8" and corresponds to 8
        // hours behind UTC (i.e. west of Greenwich) even though many people would expect it to mean 8 hours ahead of
        // UTC (i.e. east of Greenwich; like Singapore or China).
        // (adapted from tzdb table comments)
        assertEquals(8 * 60 * 60, output.getOffsets().get("Etc/GMT-8").intValue());
        assertEquals(-5 * 60 * 60, output.getOffsets().get("Etc/GMT+5").intValue());
        assertEquals(11 * 60 * 60, output.getOffsets().get("Etc/GMT-11").intValue());
        assertEquals(0, output.getOffsets().get("Etc/GMT+0").intValue());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}
