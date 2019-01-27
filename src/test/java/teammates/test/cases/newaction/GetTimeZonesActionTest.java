package teammates.test.cases.newaction;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.GetTimeZonesAction;
import teammates.ui.newcontroller.GetTimeZonesAction.TimezoneData;
import teammates.ui.newcontroller.JsonResult;

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

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        // This test does not check the timezone database used is the latest
        // Only check that the version number is returned, and some sample values for timezone offset

        TimezoneData output = (TimezoneData) r.getOutput();
        Map<String, Integer> offsets = output.getOffsets();
        assertNotNull(output.getVersion());
        assertEquals(8 * 60 * 60, offsets.get("Asia/Singapore").intValue());
        assertEquals(-5 * 60 * 60, offsets.get("America/New_York").intValue());
        assertEquals(11 * 60 * 60, offsets.get("Australia/Sydney").intValue());
        assertEquals(0, offsets.get("Europe/London").intValue());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

}
