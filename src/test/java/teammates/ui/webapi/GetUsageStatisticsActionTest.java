package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link GetUsageStatisticsAction}.
 */
public class GetUsageStatisticsActionTest extends BaseActionTest<GetUsageStatisticsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.USAGE_STATISTICS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAccessibleForAdmin();
        verifyAccessibleForMaintainers();
        verifyInaccessibleForStudents();
        verifyInaccessibleForInstructors();
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }

    @Override
    @Test(enabled = false)
    public void testExecute() throws Exception {

        loginAsAdmin();

        long startTimeForFailCases = Instant.now().minusSeconds(60).toEpochMilli();
        long endTimeForFailCases = startTimeForFailCases - 1000;

        ______TS("Failure case: end time is before start time");

        String[] paramsInvalid1 = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        verifyHttpParameterFailure(paramsInvalid1);

        ______TS("Failure case: invalid search start time");

        String[] paramsInvalid2 = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, "abc",
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        verifyHttpParameterFailure(paramsInvalid2);

        ______TS("Failure case: invalid search end time");

        String[] paramsInvalid3 = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, " ",
        };
        verifyHttpParameterFailure(paramsInvalid3);

        ______TS("Failure case: search window too long");

        long millisExceedingSearchWindow = Duration.ofDays(200L).toMillis();
        String[] paramsInvalid4 = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(startTimeForFailCases + millisExceedingSearchWindow),
        };
        verifyHttpParameterFailure(paramsInvalid4);

        ______TS("Failure case: end time after current time");

        long millisExceedingNow = Instant.now().plusMillis(1000).toEpochMilli();
        String[] paramsInvalid5 = {
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(millisExceedingNow),
        };
        verifyHttpParameterFailure(paramsInvalid5);

        ______TS("Success case");

        GetUsageStatisticsAction action = getAction(
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(startTimeForFailCases + 1000)
        );
        // For now, we stop at simply checking that the request is successful,
        // as we do not have means to reliably create test usage attributes data yet.
        getJsonResult(action);

    }

}
