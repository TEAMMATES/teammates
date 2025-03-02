package teammates.sqlui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.TimeZonesData;
import teammates.ui.webapi.GetTimeZonesAction;
import teammates.ui.webapi.JsonResult;

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

    @Test
    void testAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_maintainers_canAccess() {
        loginAsMaintainer();
        verifyCanAccess();
    }

    @Test
    void testAccessControl_instructor_cannotAccess() {
        loginAsInstructor(Const.ParamsNames.INSTRUCTOR_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        loginAsStudent(Const.ParamsNames.STUDENT_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_unregistered_cannotAccess() {
        loginAsUnregistered(Const.ParamsNames.USER_ID);
        verifyCannotAccess();
    }

    @Test
    protected void testExecute_normalCase_shouldSucceed() {
        GetTimeZonesAction a = getAction();
        JsonResult r = getJsonResult(a);

        TimeZonesData output = (TimeZonesData) r.getOutput();

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
}
