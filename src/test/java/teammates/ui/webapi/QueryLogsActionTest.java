package teammates.ui.webapi;

import java.time.Instant;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;

/**
 * SUT: {@link QueryLogsAction}.
 */
public class QueryLogsActionTest extends BaseActionTest<QueryLogsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        JsonResult actionOutput;

        String severities = "INFO,ERROR";
        long startTimeForFailCases = Instant.now().toEpochMilli();
        long endTimeForFailCases = startTimeForFailCases - 1000;
        long endTimeForSuccessCases = Instant.now().toEpochMilli();
        long startTimeForSuccessCases = endTimeForSuccessCases - 1000 * 60 * 60 * 24;

        ______TS("Failure case: search end time is before search start time");
        String[] paramsInvalid1 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getJsonResult(getAction(paramsInvalid1)));

        ______TS("Failure case: invalid search start time");
        String[] paramsInvalid2 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, "abc",
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        actionOutput = getJsonResult(getAction(paramsInvalid2));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        ______TS("Failure case: invalid search end time");
        String[] paramsInvalid3 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, " ",
        };
        actionOutput = getJsonResult(getAction(paramsInvalid3));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        ______TS("Success case: all HTTP parameters are valid");
        String[] paramsSuccessful1 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful1));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}
