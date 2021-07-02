package teammates.ui.webapi;

import com.google.cloud.logging.Severity;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.test.MockLogsProcessor;

import java.time.Instant;
import java.util.List;

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
        String trace1 = "trace1";
        String trace2 = "trace2";
        String sourceLocation1 = "sourceLocation1";
        String sourceLocation2 = "sourceLocation2";
        String infoLogPayload = "info log payload";
        String warningLogPayload = "warning log payload";
        String errorLogPayload = "error log payload";
        long startTimeForFailCases = Instant.now().toEpochMilli();
        long endTimeForFailCases = startTimeForFailCases - 1000;
        long endTimeForSuccessCases = Instant.now().toEpochMilli();
        long startTimeForSuccessCases = endTimeForSuccessCases - 1000 * 60 * 60 * 24;
        long logTimestamp = endTimeForSuccessCases - 1000 * 60;

        mockLogsProcessor.insertInfoLog(trace1, sourceLocation1, infoLogPayload, logTimestamp);
        mockLogsProcessor.insertInfoLog(trace2, sourceLocation2, infoLogPayload, logTimestamp);
        mockLogsProcessor.insertWarningLog(trace1, sourceLocation1, warningLogPayload, logTimestamp);
        mockLogsProcessor.insertWarningLog(trace2, sourceLocation2, warningLogPayload, logTimestamp);
        mockLogsProcessor.insertErrorLog(trace1, sourceLocation1, errorLogPayload, logTimestamp);
        mockLogsProcessor.insertErrorLog(trace2, sourceLocation2, errorLogPayload, logTimestamp);

        ______TS("Failure case: search end time is before search start time");
        String[] paramsInvalid1 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases)
        };
        assertThrows(InvalidHttpParameterException.class, () -> getJsonResult(getAction(paramsInvalid1)));

        ______TS("Failure case: invalid search start time");
        String[] paramsInvalid2 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, "abc",
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases)
        };
        actionOutput = getJsonResult(getAction(paramsInvalid2));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        ______TS("Failure case: invalid search end time");
        String[] paramsInvalid3 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, " "
        };
        actionOutput = getJsonResult(getAction(paramsInvalid3));
        assertEquals(HttpStatus.SC_BAD_REQUEST, actionOutput.getStatusCode());

        ______TS("Success case: all HTTP parameters are valid");
        String[] paramsSuccessful1 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITIES, severities,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases)
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful1));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());
        GeneralLogEntry a = new GeneralLogEntry("a", Severity.INFO, "a", null, null, 1L);
//        MockLogsProcessor.MockGeneralLogEntry logEntries = (MockLogsProcessor.MockQueryResults) actionOutput.getOutput();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}
