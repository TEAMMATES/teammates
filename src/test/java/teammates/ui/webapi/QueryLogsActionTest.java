package teammates.ui.webapi;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.datatransfer.logs.SourceLocation;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
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
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("starttime", 1);
        requestParams.put("endtime", 2);
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("User-Agent", "user agent");
        requestHeaders.put("Host", "host");

        String severity = "INFO";
        String infoLogTrace1 = "info log trace 1";
        String infoLogTrace2 = "info log trace 2";
        String infoLogInsertId1 = "info log insert id 1";
        String infoLogInsertId2 = "info log insert id 2";
        String infoLogTextPayload1 = "info log text palyload 1";
        String infoLogTextPayload2 = "info log text palyload 2";
        SourceLocation infoLogSourceLocation1 = new SourceLocation("file1", 1L, "func1");
        SourceLocation infoLogSourceLocation2 = new SourceLocation("file2", 2L, "func2");
        long infoLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 1;
        long infoLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 2;
        Map<String, String> infoLogUserInfo1 = new HashMap<>();
        infoLogUserInfo1.put("googleId", "infoLogUserGoogleId");
        Map<String, String> infoLogUserInfo2 = new HashMap<>();
        infoLogUserInfo2.put("regkey", "infoLogUserRegkey");
        infoLogUserInfo2.put("email", "infoLogUserEmail");
        Map<String, Object> infoLogJsonPayLoad1 = new HashMap<>();
        infoLogJsonPayLoad1.put("requestParams", requestParams);
        infoLogJsonPayLoad1.put("requestHeaders", requestHeaders);
        infoLogJsonPayLoad1.put("requestMethod", "GET");
        infoLogJsonPayLoad1.put("responseStatus", 200);
        infoLogJsonPayLoad1.put("responseTime", 10);
        infoLogJsonPayLoad1.put("message", "info log json payload message 1");
        infoLogJsonPayLoad1.put("userInfo", infoLogUserInfo1);
        infoLogJsonPayLoad1.put("actionClass", "infoLogActionClass1");
        infoLogJsonPayLoad1.put("event", LogEvent.REQUEST_LOG.toString());
        Map<String, Object> infoLogJsonPayLoad2 = new HashMap<>();
        infoLogJsonPayLoad2.put("requestParams", requestParams);
        infoLogJsonPayLoad2.put("requestHeaders", requestHeaders);
        infoLogJsonPayLoad2.put("requestMethod", "POST");
        infoLogJsonPayLoad2.put("responseStatus", 404);
        infoLogJsonPayLoad2.put("responseTime", 20);
        infoLogJsonPayLoad2.put("message", "info log json payload message 2");
        infoLogJsonPayLoad2.put("userInfo", infoLogUserInfo2);
        infoLogJsonPayLoad2.put("actionClass", "infoLogActionClass2");
        infoLogJsonPayLoad2.put("event", LogEvent.FEEDBACK_SESSION_AUDIT.toString());
        infoLogJsonPayLoad2.put("studentEmail", "student email");

        String warningLogTrace1 = "warning log trace 1";
        String warningLogTrace2 = "warning log trace 2";
        String warningLogInsertId1 = "warning log insert id 1";
        String warningLogInsertId2 = "warning log insert id 2";
        String warningLogTextPayload1 = "warning log text palyload 1";
        String warningLogTextPayload2 = "warning log text palyload 2";
        SourceLocation warningLogSourceLocation1 = new SourceLocation("file3", 3L, "func3");
        SourceLocation warningLogSourceLocation2 = new SourceLocation("file4", 4L, "func4");
        long warningLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 3;
        long warningLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 4;
        Map<String, String> warningLogUserInfo1 = new HashMap<>();
        warningLogUserInfo1.put("googleId", "warningLogUserGoogleId");
        Map<String, String> warningLogUserInfo2 = new HashMap<>();
        warningLogUserInfo2.put("regkey", "warningLogUserRegkey");
        warningLogUserInfo2.put("email", "warningLogUserEmail");
        Map<String, Object> warningLogJsonPayLoad1 = new HashMap<>();
        warningLogJsonPayLoad1.put("requestParams", requestParams);
        warningLogJsonPayLoad1.put("requestHeaders", requestHeaders);
        warningLogJsonPayLoad1.put("requestMethod", "GET");
        warningLogJsonPayLoad1.put("responseStatus", 404);
        warningLogJsonPayLoad1.put("responseTime", 30);
        warningLogJsonPayLoad1.put("message", "warning log json payload message 1");
        warningLogJsonPayLoad1.put("userInfo", warningLogUserInfo1);
        warningLogJsonPayLoad1.put("actionClass", "warningLogActionClass1");
        warningLogJsonPayLoad1.put("event", LogEvent.REQUEST_LOG.toString());
        Map<String, Object> warningLogJsonPayLoad2 = new HashMap<>();
        warningLogJsonPayLoad2.put("message", "warning log json payload message 2");
        warningLogJsonPayLoad2.put("userInfo", warningLogUserInfo2);
        warningLogJsonPayLoad2.put("actionClass", "warningLogActionClass2");
        warningLogJsonPayLoad2.put("event", LogEvent.EMAIL_SENT.toString());
        warningLogJsonPayLoad2.put("emailDetails", warningLogUserInfo2);
        warningLogJsonPayLoad2.put("emailStatus", 200);

        String errorLogTrace = "error log trace";
        String errorLogInsertId1 = "error log insertId 1";
        String errorLogInsertId2 = "error log insertId 2";
        String errorLogTextPayload1 = "error log text palyload 1";
        String errorLogTextPayload2 = "error log text palyload 2";
        SourceLocation errorLogSourceLocation1 = new SourceLocation("file5", 5L, "func5");
        SourceLocation errorLogSourceLocation2 = new SourceLocation("file6", 6L, "func6");
        long errorLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 5;
        long errorLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 6;
        Map<String, String> errorLogUserInfo1 = new HashMap<>();
        errorLogUserInfo1.put("googleId", "errorLogUserGoogleId");
        Map<String, String> errorLogUserInfo2 = new HashMap<>();
        errorLogUserInfo2.put("regkey", "errorLogUserRegkey");
        errorLogUserInfo2.put("email", "errorLogUserEmail");
        Map<String, Object> errorLogJsonPayLoad1 = new HashMap<>();
        errorLogJsonPayLoad1.put("requestParams", requestParams);
        errorLogJsonPayLoad1.put("requestHeaders", requestHeaders);
        errorLogJsonPayLoad1.put("requestMethod", "GET");
        errorLogJsonPayLoad1.put("responseStatus", 403);
        errorLogJsonPayLoad1.put("responseTime", 50);
        errorLogJsonPayLoad1.put("message", "error log json payload message 1");
        errorLogJsonPayLoad1.put("userInfo", errorLogUserInfo1);
        errorLogJsonPayLoad1.put("actionClass", "errorLogActionClass1");
        errorLogJsonPayLoad1.put("event", LogEvent.REQUEST_LOG.toString());
        Map<String, Object> errorLogJsonPayLoad2 = new HashMap<>();
        errorLogJsonPayLoad2.put("requestParams", requestParams);
        errorLogJsonPayLoad2.put("requestHeaders", requestHeaders);
        errorLogJsonPayLoad2.put("requestMethod", "POST");
        errorLogJsonPayLoad2.put("responseStatus", 400);
        errorLogJsonPayLoad2.put("responseTime", 60);
        errorLogJsonPayLoad2.put("message", "error log json payload message 2");
        errorLogJsonPayLoad2.put("userInfo", errorLogUserInfo2);
        errorLogJsonPayLoad2.put("actionClass", "errorLogActionClass2");
        errorLogJsonPayLoad2.put("event", LogEvent.REQUEST_LOG.toString());

        mockLogsProcessor.insertInfoLog(infoLogTrace1, infoLogInsertId1, infoLogSourceLocation1, infoLogTimestamp1,
                infoLogTextPayload1, infoLogJsonPayLoad1);
        mockLogsProcessor.insertInfoLog(infoLogTrace2, infoLogInsertId2, infoLogSourceLocation2, infoLogTimestamp2,
                infoLogTextPayload2, infoLogJsonPayLoad2);
        mockLogsProcessor.insertWarningLog(warningLogTrace1, warningLogInsertId1, warningLogSourceLocation1,
                warningLogTimestamp1, warningLogTextPayload1, warningLogJsonPayLoad1);
        mockLogsProcessor.insertWarningLog(warningLogTrace2, warningLogInsertId2, warningLogSourceLocation2,
                warningLogTimestamp2, warningLogTextPayload2, warningLogJsonPayLoad2);
        mockLogsProcessor.insertGeneralErrorLog(errorLogTrace, errorLogInsertId1, errorLogSourceLocation1,
                errorLogTimestamp1, errorLogTextPayload1, errorLogJsonPayLoad1);
        mockLogsProcessor.insertGeneralErrorLog(errorLogTrace, errorLogInsertId2, errorLogSourceLocation2,
                errorLogTimestamp2, errorLogTextPayload2, errorLogJsonPayLoad2);

        loginAsAdmin();

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

        ______TS("Success case: all fields are visible to admin");
        logoutUser();
        loginAsAdmin();
        String[] paramsForAdmin = {
                Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY, severity,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases),
        };
        actionOutput = getJsonResult(getAction(paramsForAdmin));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        generalLogsData = (GeneralLogsData) actionOutput.getOutput();
        logEntries = generalLogsData.getLogEntries();

        assertEquals(6, logEntries.size());

        entry1 = logEntries.get(0);
        entry2 = logEntries.get(1);
        entry3 = logEntries.get(2);
        entry4 = logEntries.get(3);
        entry5 = logEntries.get(4);
        entry6 = logEntries.get(5);

        assertEquals(infoLogJsonPayLoad1, entry1.getDetails());
        assertEquals(infoLogJsonPayLoad2, entry2.getDetails());
        assertEquals(warningLogJsonPayLoad1, entry3.getDetails());
        assertEquals(warningLogJsonPayLoad2, entry4.getDetails());
        assertEquals(errorLogJsonPayLoad1, entry5.getDetails());
        assertEquals(errorLogJsonPayLoad2, entry6.getDetails());

        ______TS("Success case: sensitive fields are hidden from non-admin maintainer");
        logoutUser();
        loginAsMaintainer();
        String[] paramsForMaintainer = {
                Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY, severity,
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases),
        };
        actionOutput = getJsonResult(getAction(paramsForMaintainer));
        assertEquals(HttpStatus.SC_OK, actionOutput.getStatusCode());

        generalLogsData = (GeneralLogsData) actionOutput.getOutput();
        logEntries = generalLogsData.getLogEntries();

        entry1 = logEntries.get(0);
        entry2 = logEntries.get(1);
        entry3 = logEntries.get(2);
        entry4 = logEntries.get(3);
        entry5 = logEntries.get(4);
        entry6 = logEntries.get(5);

        assertEquals(6, logEntries.size());

        assertEquals(infoLogJsonPayLoad1.get("requestMethod"), entry1.getDetails().get("requestMethod"));
        assertEquals(infoLogJsonPayLoad1.get("responseStatus"), entry1.getDetails().get("responseStatus"));
        assertEquals(infoLogJsonPayLoad1.get("responseTime"), entry1.getDetails().get("responseTime"));
        assertEquals(infoLogJsonPayLoad1.get("actionClass"), entry1.getDetails().get("actionClass"));
        assertEquals(entry1.getDetails().get("message"), entry1.getDetails().get("message"));
        assertNull(entry1.getDetails().get("requestParams"));
        assertNull(entry1.getDetails().get("requestHeaders"));
        assertNull(entry1.getDetails().get("userInfo"));
        assertNull(entry1.getMessage());

        assertEquals(infoLogJsonPayLoad2.get("requestMethod"), entry2.getDetails().get("requestMethod"));
        assertEquals(infoLogJsonPayLoad2.get("responseStatus"), entry2.getDetails().get("responseStatus"));
        assertEquals(infoLogJsonPayLoad2.get("responseTime"), entry2.getDetails().get("responseTime"));
        assertEquals(infoLogJsonPayLoad2.get("actionClass"), entry2.getDetails().get("actionClass"));
        assertEquals(entry2.getDetails().get("message"), entry2.getDetails().get("message"));
        assertNull(entry2.getDetails().get("requestParams"));
        assertNull(entry2.getDetails().get("requestHeaders"));
        assertNull(entry2.getDetails().get("userInfo"));
        assertNull(entry2.getDetails().get("studentEmail"));
        assertNull(entry1.getMessage());

        assertEquals(warningLogJsonPayLoad1.get("requestMethod"), entry3.getDetails().get("requestMethod"));
        assertEquals(warningLogJsonPayLoad1.get("responseStatus"), entry3.getDetails().get("responseStatus"));
        assertEquals(warningLogJsonPayLoad1.get("responseTime"), entry3.getDetails().get("responseTime"));
        assertEquals(warningLogJsonPayLoad1.get("actionClass"), entry3.getDetails().get("actionClass"));
        assertEquals(entry3.getDetails().get("message"), entry3.getDetails().get("message"));
        assertNull(entry3.getDetails().get("requestParams"));
        assertNull(entry3.getDetails().get("requestHeaders"));
        assertNull(entry3.getDetails().get("userInfo"));
        assertNull(entry1.getMessage());

        assertEquals(warningLogJsonPayLoad2.get("requestMethod"), entry4.getDetails().get("requestMethod"));
        assertEquals(warningLogJsonPayLoad2.get("responseStatus"), entry4.getDetails().get("responseStatus"));
        assertEquals(warningLogJsonPayLoad2.get("responseTime"), entry4.getDetails().get("responseTime"));
        assertEquals(warningLogJsonPayLoad2.get("actionClass"), entry4.getDetails().get("actionClass"));
        assertEquals(warningLogJsonPayLoad2.get("message"), entry4.getDetails().get("message"));
        assertNull(entry4.getDetails().get("requestParams"));
        assertNull(entry4.getDetails().get("requestHeaders"));
        assertNull(entry4.getDetails().get("userInfo"));
        assertNull(entry4.getDetails().get("emailDetails"));
        assertNull(entry1.getMessage());

        assertEquals(errorLogJsonPayLoad1.get("requestMethod"), entry5.getDetails().get("requestMethod"));
        assertEquals(errorLogJsonPayLoad1.get("responseStatus"), entry5.getDetails().get("responseStatus"));
        assertEquals(errorLogJsonPayLoad1.get("responseTime"), entry5.getDetails().get("responseTime"));
        assertEquals(errorLogJsonPayLoad1.get("actionClass"), entry5.getDetails().get("actionClass"));
        assertEquals(entry5.getDetails().get("message"), entry5.getDetails().get("message"));
        assertNull(entry5.getDetails().get("requestParams"));
        assertNull(entry5.getDetails().get("requestHeaders"));
        assertNull(entry5.getDetails().get("userInfo"));
        assertNull(entry1.getMessage());

        assertEquals(errorLogJsonPayLoad2.get("requestMethod"), entry6.getDetails().get("requestMethod"));
        assertEquals(errorLogJsonPayLoad2.get("responseStatus"), entry6.getDetails().get("responseStatus"));
        assertEquals(errorLogJsonPayLoad2.get("responseTime"), entry6.getDetails().get("responseTime"));
        assertEquals(errorLogJsonPayLoad2.get("actionClass"), entry6.getDetails().get("actionClass"));
        assertEquals(entry6.getDetails().get("message"), entry6.getDetails().get("message"));
        assertNull(entry6.getDetails().get("requestParams"));
        assertNull(entry6.getDetails().get("requestHeaders"));
        assertNull(entry6.getDetails().get("userInfo"));
        assertNull(entry1.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAccessibleForAdmin();
        verifyAccessibleForMaintainers();
        verifyInaccessibleForStudents();
        verifyInaccessibleForInstructors();
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
