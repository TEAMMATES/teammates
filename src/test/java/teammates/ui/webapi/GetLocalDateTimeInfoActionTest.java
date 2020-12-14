package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.LocalDateTimeInfo;

/**
 * SUT: {@link GetLocalDateTimeInfoAction}.
 */
public class GetLocalDateTimeInfoActionTest extends BaseActionTest<GetLocalDateTimeInfoAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LOCAL_DATE_TIME;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.LOCAL_DATE_TIME, "2018-12-01 13:00");
        verifyHttpParameterFailure(Const.ParamsNames.TIME_ZONE, "UTC");

        ______TS("Failure: DateTimeException with invalid time or timeZone");

        verifyHttpParameterFailure(Const.ParamsNames.LOCAL_DATE_TIME, "random",
                Const.ParamsNames.TIME_ZONE, "UTC");
        verifyHttpParameterFailure(Const.ParamsNames.LOCAL_DATE_TIME, "2018-12-01 13:00",
                Const.ParamsNames.TIME_ZONE, "random");

        ______TS("UNAMBIGUOUS status");

        String[] params = new String[] {
                Const.ParamsNames.LOCAL_DATE_TIME, "2018-12-01 13:00",
                Const.ParamsNames.TIME_ZONE, "UTC",
        };

        GetLocalDateTimeInfoAction a = getAction(params);
        JsonResult r = getJsonResult(a);
        LocalDateTimeInfo localDateTimeInfo = (LocalDateTimeInfo) r.getOutput();

        assertEquals(LocalDateTimeInfo.LocalDateTimeAmbiguityStatus.UNAMBIGUOUS, localDateTimeInfo.getResolvedStatus());
        assertEquals(1543669200000L, localDateTimeInfo.getResolvedTimestamp());
        assertNull(localDateTimeInfo.getEarlierInterpretationTimestamp());
        assertNull(localDateTimeInfo.getLaterInterpretationTimestamp());

        ______TS("GAP status");

        // After 2012-03-25 01:59:59, clocks sprang forward to 2012-03-25 03:00:00 in Europe/Andorra

        params = new String[] {
                // this time does't not exist in Europe/Andorra with DST
                Const.ParamsNames.LOCAL_DATE_TIME, "2012-03-25 02:30",
                Const.ParamsNames.TIME_ZONE, "Europe/Andorra",
        };

        a = getAction(params);
        r = getJsonResult(a);
        localDateTimeInfo = (LocalDateTimeInfo) r.getOutput();

        assertEquals(LocalDateTimeInfo.LocalDateTimeAmbiguityStatus.GAP, localDateTimeInfo.getResolvedStatus());
        assertEquals(1332639000000L, localDateTimeInfo.getResolvedTimestamp());
        assertNull(localDateTimeInfo.getEarlierInterpretationTimestamp());
        assertNull(localDateTimeInfo.getLaterInterpretationTimestamp());

        ______TS("OVERLAP status");

        // After 2012-10-28 02:59:59, clocks fell back to 2012-10-28 02:00:00 in Europe/Andorra

        params = new String[] {
                // this time has two meanings in Europe/Andorra with DST
                Const.ParamsNames.LOCAL_DATE_TIME, "2012-10-28 02:30",
                Const.ParamsNames.TIME_ZONE, "Europe/Andorra",
        };

        a = getAction(params);
        r = getJsonResult(a);
        localDateTimeInfo = (LocalDateTimeInfo) r.getOutput();

        assertEquals(LocalDateTimeInfo.LocalDateTimeAmbiguityStatus.OVERLAP, localDateTimeInfo.getResolvedStatus());
        assertEquals(1351384200000L, localDateTimeInfo.getResolvedTimestamp());
        assertEquals(1351384200000L, localDateTimeInfo.getEarlierInterpretationTimestamp().longValue());
        assertEquals(1351387800000L, localDateTimeInfo.getLaterInterpretationTimestamp().longValue());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.LOCAL_DATE_TIME, "2018-12-01 12:23",
                Const.ParamsNames.TIME_ZONE, "Asia/Singapore",
        };

        ______TS("User need to be logged in");

        gaeSimulation.logoutUser();
        verifyInaccessibleWithoutLogin(params);

        ______TS("Only user who is instructor can access");

        verifyInaccessibleForStudents(params);
        verifyInaccessibleForUnregisteredUsers(params);

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(params);
    }
}
