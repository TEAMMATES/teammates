package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.GetTimeZonesAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.TimeZonesData;

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

        /* TODO the asserts below are brittle as the expected values are not guaranteed to be correct
         * e.g. New York observes DST, so the offset is not always UTC-05:00 the entire year.
         * e.g. timezones can change, like Caracas modifying their timezone. This affects the offset as well.
         */
        assertEquals(8 * 60 * 60, output.getOffsets().get("Asia/Singapore").intValue());
        assertEquals(-5 * 60 * 60, output.getOffsets().get("America/New_York").intValue());
        assertEquals(11 * 60 * 60, output.getOffsets().get("Australia/Sydney").intValue());
        assertEquals(0, output.getOffsets().get("Europe/London").intValue());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}
