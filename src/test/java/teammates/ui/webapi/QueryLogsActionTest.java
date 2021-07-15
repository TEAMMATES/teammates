package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.LogEvent;
import teammates.ui.output.GeneralLogsData;

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

        long startTimeForFailCases = Instant.now().toEpochMilli();
        long endTimeForFailCases = startTimeForFailCases - 1000;
        long endTimeForSuccessCases = Instant.now().toEpochMilli();
        long startTimeForSuccessCases = endTimeForSuccessCases - 1000 * 60 * 60 * 24;

        String severity = "INFO";
        String infoLogTrace1 = "info log trace 1";
        String infoLogTrace2 = "info log trace 2";
        String infoLogTextPayload1 = "info log text palyload 1";
        String infoLogTextPayload2 = "info log text palyload 2";
        GeneralLogEntry.SourceLocation infoLogSourceLocation1 = new GeneralLogEntry.SourceLocation("file1", 1L, "func1");
        GeneralLogEntry.SourceLocation infoLogSourceLocation2 = new GeneralLogEntry.SourceLocation("file2", 2L, "func2");
        long infoLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 1;
        long infoLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 2;
        String infoLogActionClass = "info log actionClass";
        String infoLogGoogleId = "info log google id";

        String warningLogTrace1 = "warning log trace 1";
        String warningLogTrace2 = "warning log trace 2";
        String warningLogTextPayload1 = "warning log text palyload 1";
        String warningLogTextPayload2 = "warning log text palyload 2";
        GeneralLogEntry.SourceLocation warningLogSourceLocation1 = new GeneralLogEntry.SourceLocation("file3", 3L, "func3");
        GeneralLogEntry.SourceLocation warningLogSourceLocation2 = new GeneralLogEntry.SourceLocation("file4", 4L, "func4");
        long warningLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 3;
        long warningLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 4;
        String warningLogRegkey = "warning log regkey";
        String warningLogEmail = "warning log email";

        String errorLogTrace = "error log trace";
        String errorLogTextPayload1 = "error log text palyload 1";
        String errorLogTextPayload2 = "error log text palyload 2";
        GeneralLogEntry.SourceLocation errorLogSourceLocation1 = new GeneralLogEntry.SourceLocation("file5", 5L, "func5");
        GeneralLogEntry.SourceLocation errorLogSourceLocation2 = new GeneralLogEntry.SourceLocation("file6", 6L, "func6");
        long errorLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 5;
        long errorLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 6;
        String errorLogExceptionClass = "error log exception class";

        mockLogsProcessor.insertInfoLog(infoLogTrace1, infoLogSourceLocation1, infoLogTimestamp1, infoLogTextPayload1,
                infoLogActionClass, infoLogGoogleId, null, null, LogEvent.REQUEST_RECEIVED.toString(), null);
        mockLogsProcessor.insertInfoLog(infoLogTrace2, infoLogSourceLocation2, infoLogTimestamp2, infoLogTextPayload2,
                infoLogActionClass, infoLogGoogleId, null, null, LogEvent.RESPONSE_DISPATCHED.toString(), null);
        mockLogsProcessor.insertWarningLog(warningLogTrace1, warningLogSourceLocation1, warningLogTimestamp1,
                warningLogTextPayload1, null, null, warningLogRegkey, warningLogEmail, null, null);
        mockLogsProcessor.insertWarningLog(warningLogTrace2, warningLogSourceLocation2, warningLogTimestamp2,
                warningLogTextPayload2, null, null, warningLogRegkey, warningLogEmail, null, null);
        mockLogsProcessor.insertGeneralErrorLog(errorLogTrace, errorLogSourceLocation1, errorLogTimestamp1,
                errorLogTextPayload1, null, null, null, null, null, errorLogExceptionClass);
        mockLogsProcessor.insertGeneralErrorLog(errorLogTrace, errorLogSourceLocation2, errorLogTimestamp2,
                errorLogTextPayload2, null, null, null, null, null, null);

        ______TS("Failure case: search end time is before search start time");
        String[] paramsInvalid1 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITY, severity,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getJsonResult(getAction(paramsInvalid1)));

        ______TS("Failure case: invalid search start time");
        String[] paramsInvalid2 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITY, severity,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, "abc",
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getJsonResult(getAction(paramsInvalid2)));

        ______TS("Failure case: invalid search end time");
        String[] paramsInvalid3 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITY, severity,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, " ",
        };
        assertThrows(InvalidHttpParameterException.class, () -> getJsonResult(getAction(paramsInvalid3)));

        ______TS("Success case: all HTTP parameters are valid; filter by minimum severity level");
        String[] paramsMinSeverity = {
                Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY, severity,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases),
        };
        actionOutput = getJsonResult(getAction(paramsMinSeverity));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        GeneralLogsData generalLogsData = (GeneralLogsData) actionOutput.getOutput();
        List<GeneralLogEntry> logEntries = generalLogsData.getLogEntries();

        assertEquals(6, logEntries.size());

        GeneralLogEntry entry1 = logEntries.get(0);
        GeneralLogEntry entry2 = logEntries.get(1);
        GeneralLogEntry entry3 = logEntries.get(2);
        GeneralLogEntry entry4 = logEntries.get(3);
        GeneralLogEntry entry5 = logEntries.get(4);
        GeneralLogEntry entry6 = logEntries.get(5);

        assertEquals("stdout", entry1.getLogName());
        assertEquals("INFO", entry1.getSeverity());
        assertEquals(infoLogTrace1, entry1.getTrace());
        assertEquals(infoLogSourceLocation1, entry1.getSourceLocation());
        assertEquals(infoLogTimestamp1, entry1.getTimestamp());

        assertEquals("stdout", entry2.getLogName());
        assertEquals("INFO", entry2.getSeverity());
        assertEquals(infoLogTrace2, entry2.getTrace());
        assertEquals(infoLogSourceLocation2, entry2.getSourceLocation());
        assertEquals(infoLogTimestamp2, entry2.getTimestamp());

        assertEquals("stderr", entry3.getLogName());
        assertEquals("WARNING", entry3.getSeverity());
        assertEquals(warningLogTrace1, entry3.getTrace());
        assertEquals(warningLogSourceLocation1, entry3.getSourceLocation());
        assertEquals(warningLogTimestamp1, entry3.getTimestamp());

        assertEquals("stderr", entry4.getLogName());
        assertEquals("WARNING", entry4.getSeverity());
        assertEquals(warningLogTrace2, entry4.getTrace());
        assertEquals(warningLogSourceLocation2, entry4.getSourceLocation());
        assertEquals(warningLogTimestamp2, entry4.getTimestamp());

        assertEquals("stderr", entry5.getLogName());
        assertEquals("ERROR", entry5.getSeverity());
        assertEquals(errorLogTrace, entry5.getTrace());
        assertEquals(errorLogSourceLocation1, entry5.getSourceLocation());
        assertEquals(errorLogTimestamp1, entry5.getTimestamp());

        assertEquals("stderr", entry6.getLogName());
        assertEquals("ERROR", entry6.getSeverity());
        assertEquals(errorLogTrace, entry6.getTrace());
        assertEquals(errorLogSourceLocation2, entry6.getSourceLocation());
        assertEquals(errorLogTimestamp2, entry6.getTimestamp());

    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}
