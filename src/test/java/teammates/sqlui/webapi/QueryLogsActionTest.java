package teammates.sqlui.webapi;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.EmailSentLogDetails;
import teammates.common.datatransfer.logs.ExceptionLogDetails;
import teammates.common.datatransfer.logs.FeedbackSessionAuditLogDetails;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.RequestLogDetails;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.datatransfer.logs.SourceLocation;
import teammates.common.util.Const;
import teammates.ui.output.GeneralLogsData;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.QueryLogsAction;

/**
 * SUT: {@link QueryLogsAction}.
 */
public class QueryLogsActionTest extends BaseActionTest<QueryLogsAction> {
    private static final String GOOGLE_ID = "user-googleId";

    private long startTimeForFailCases = Instant.now().toEpochMilli();
    private long endTimeForFailCases = startTimeForFailCases - 1000;
    private long endTimeForSuccessCases = Instant.now().toEpochMilli();
    private long startTimeForSuccessCases = endTimeForSuccessCases - 1000 * 60 * 60 * 24;
    private Map<String, Object> requestParams = new HashMap<>();
    private Map<String, Object> requestHeaders = new HashMap<>();

    private String infoLogTrace1 = "info log trace 1";
    private String infoLogTrace2 = "info log trace 2";
    private String infoLogInsertId1 = "info log insert id 1";
    private String infoLogInsertId2 = "info log insert id 2";
    private String infoLogTextPayload1 = "info log text payload 1";
    private String infoLogTextPayload2 = "info log text payload 2";
    private SourceLocation infoLogSourceLocation1 = new SourceLocation("file1", 1L, "func1");
    private SourceLocation infoLogSourceLocation2 = new SourceLocation("file2", 2L, "func2");
    private long infoLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 1;
    private long infoLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 2;
    private RequestLogUser infoLogUserInfo1 = new RequestLogUser();
    private RequestLogDetails infoLogJsonPayLoad1 = new RequestLogDetails();

    private FeedbackSessionAuditLogDetails infoLogJsonPayLoad2 = new FeedbackSessionAuditLogDetails();
    private String warningLogTrace1 = "warning log trace 1";
    private String warningLogTrace2 = "warning log trace 2";
    private String warningLogInsertId1 = "warning log insert id 1";
    private String warningLogInsertId2 = "warning log insert id 2";
    private String warningLogTextPayload1 = "warning log text payload 1";
    private String warningLogTextPayload2 = "warning log text payload 2";
    private SourceLocation warningLogSourceLocation1 = new SourceLocation("file3", 3L, "func3");
    private SourceLocation warningLogSourceLocation2 = new SourceLocation("file4", 4L, "func4");
    private long warningLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 3;
    private long warningLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 4;
    private RequestLogUser warningLogUserInfo1 = new RequestLogUser();
    private RequestLogDetails warningLogJsonPayLoad1 = new RequestLogDetails();

    private EmailSentLogDetails warningLogJsonPayLoad2 = new EmailSentLogDetails();
    private String errorLogTrace = "error log trace";
    private String errorLogInsertId1 = "error log insertId 1";
    private String errorLogInsertId2 = "error log insertId 2";
    private String errorLogTextPayload1 = "error log text payload 1";
    private String errorLogTextPayload2 = "error log text payload 2";
    private SourceLocation errorLogSourceLocation1 = new SourceLocation("file5", 5L, "func5");
    private SourceLocation errorLogSourceLocation2 = new SourceLocation("file6", 6L, "func6");
    private long errorLogTimestamp1 = endTimeForSuccessCases - 1000 * 60 - 5;
    private long errorLogTimestamp2 = endTimeForSuccessCases - 1000 * 60 - 6;
    private RequestLogUser errorLogUserInfo1 = new RequestLogUser();
    private RequestLogDetails errorLogJsonPayLoad1 = new RequestLogDetails();
    private ExceptionLogDetails errorLogJsonPayLoad2 = new ExceptionLogDetails();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeClass
    void setUp() {
        requestParams.put("starttime", 1);
        requestParams.put("endtime", 2);

        requestHeaders.put("User-Agent", "user agent");
        requestHeaders.put("Host", "host");

        infoLogUserInfo1.setGoogleId("infoLogUserGoogleId");

        infoLogJsonPayLoad1.setRequestParams(requestParams);
        infoLogJsonPayLoad1.setRequestHeaders(requestHeaders);
        infoLogJsonPayLoad1.setRequestMethod("GET");
        infoLogJsonPayLoad1.setResponseStatus(200);
        infoLogJsonPayLoad1.setResponseTime(10);
        infoLogJsonPayLoad1.setMessage("info log json payload message 1");
        infoLogJsonPayLoad1.setUserInfo(infoLogUserInfo1);
        infoLogJsonPayLoad1.setActionClass("infoLogActionClass1");

        infoLogJsonPayLoad2.setMessage("info log json payload message 2");
        infoLogJsonPayLoad2.setStudentEmail("student.email@example.com");
        infoLogJsonPayLoad2.setCourseId("course.id");
        infoLogJsonPayLoad2.setFeedbackSessionName("feedback session name");

        warningLogUserInfo1.setGoogleId("warningLogUserGoogleId");

        warningLogJsonPayLoad1.setRequestParams(requestParams);
        warningLogJsonPayLoad1.setRequestHeaders(requestHeaders);
        warningLogJsonPayLoad1.setRequestMethod("GET");
        warningLogJsonPayLoad1.setResponseStatus(404);
        warningLogJsonPayLoad1.setResponseTime(30);
        warningLogJsonPayLoad1.setMessage("warning log json payload message 1");
        warningLogJsonPayLoad1.setUserInfo(warningLogUserInfo1);
        warningLogJsonPayLoad1.setActionClass("warningLogActionClass1");

        warningLogJsonPayLoad2.setMessage("warning log json payload message 2");
        warningLogJsonPayLoad2.setEmailStatusMessage("OK");
        warningLogJsonPayLoad2.setEmailStatus(200);
        warningLogJsonPayLoad2.setEmailContent("email content");
        warningLogJsonPayLoad2.setEmailRecipient("email.recipient@example.com");
        warningLogJsonPayLoad2.setEmailSubject("email subject");

        errorLogUserInfo1.setGoogleId("errorLogUserGoogleId");

        errorLogJsonPayLoad1.setRequestParams(requestParams);
        errorLogJsonPayLoad1.setRequestHeaders(requestHeaders);
        errorLogJsonPayLoad1.setRequestMethod("GET");
        errorLogJsonPayLoad1.setResponseStatus(403);
        errorLogJsonPayLoad1.setResponseTime(50);
        errorLogJsonPayLoad1.setMessage("error log json payload message 1");
        errorLogJsonPayLoad1.setUserInfo(errorLogUserInfo1);
        errorLogJsonPayLoad1.setActionClass("errorLogActionClass1");

        errorLogJsonPayLoad2.setExceptionClass("exceptionClass");
        errorLogJsonPayLoad2.setExceptionMessages(Collections.singletonList("message"));
        errorLogJsonPayLoad2.setExceptionStackTraces(Collections.singletonList(
                Arrays.asList("stack trace 1", "stack trace 2", "stack trace 3")));
        errorLogJsonPayLoad2.setExceptionClasses(Collections.singletonList("exceptionClass"));
        errorLogJsonPayLoad2.setMessage("message");

        mockLogsProcessor.insertInfoLog(infoLogTrace1, infoLogInsertId1, infoLogSourceLocation1, infoLogTimestamp1,
                infoLogTextPayload1, infoLogJsonPayLoad1);
        mockLogsProcessor.insertInfoLog(infoLogTrace2, infoLogInsertId2, infoLogSourceLocation2, infoLogTimestamp2,
                infoLogTextPayload2, infoLogJsonPayLoad2);
        mockLogsProcessor.insertWarningLog(warningLogTrace1, warningLogInsertId1, warningLogSourceLocation1,
                warningLogTimestamp1, warningLogTextPayload1, warningLogJsonPayLoad1);
        mockLogsProcessor.insertWarningLog(warningLogTrace2, warningLogInsertId2, warningLogSourceLocation2,
                warningLogTimestamp2, warningLogTextPayload2, warningLogJsonPayLoad2);
        mockLogsProcessor.insertErrorLog(errorLogTrace, errorLogInsertId1, errorLogSourceLocation1,
                errorLogTimestamp1, errorLogTextPayload1, errorLogJsonPayLoad1);
        mockLogsProcessor.insertErrorLog(errorLogTrace, errorLogInsertId2, errorLogSourceLocation2,
                errorLogTimestamp2, errorLogTextPayload2, errorLogJsonPayLoad2);
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_maintainers_canAccess() {
        loginAsMaintainer();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_instructor_cannotAccess() {
        loginAsInstructor(GOOGLE_ID);
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent(GOOGLE_ID);
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_unregistered_cannotAccess() {
        loginAsUnregistered(GOOGLE_ID);
        verifyCannotAccess();
    }

    @Test
    void testExecute_searchEndTimeBeforeStart_shouldFail() {
        String[] paramsInvalid1 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITY, String.valueOf(LogSeverity.INFO),
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        verifyHttpParameterFailure(paramsInvalid1);
    }

    @Test
    void testExecute_invalidSearchStartTime_shouldFail() {
        String[] paramsInvalid2 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITY, String.valueOf(LogSeverity.INFO),
                Const.ParamsNames.QUERY_LOGS_STARTTIME, "abc",
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForFailCases),
        };
        verifyHttpParameterFailure(paramsInvalid2);
    }

    @Test
    void testExecute_invalidSearchEndTime_shouldFail() {
        String[] paramsInvalid3 = {
                Const.ParamsNames.QUERY_LOGS_SEVERITY, String.valueOf(LogSeverity.INFO),
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForFailCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, " ",
        };
        verifyHttpParameterFailure(paramsInvalid3);
    }

    @Test
    void testExecute_validParameters_shouldSucceedAndFilterByMinimumSeverity() {
        logoutUser();
        loginAsAdmin();
        String[] paramsValid = {
                Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY, String.valueOf(LogSeverity.INFO),
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases),
        };
        JsonResult actionOutput = getJsonResult(getAction(paramsValid));

        GeneralLogsData generalLogsData = (GeneralLogsData) actionOutput.getOutput();
        List<GeneralLogEntry> logEntries = generalLogsData.getLogEntries();

        assertEquals(6, logEntries.size());

        GeneralLogEntry entry1 = logEntries.get(0);
        GeneralLogEntry entry2 = logEntries.get(1);
        GeneralLogEntry entry3 = logEntries.get(2);
        GeneralLogEntry entry4 = logEntries.get(3);
        GeneralLogEntry entry5 = logEntries.get(4);
        GeneralLogEntry entry6 = logEntries.get(5);

        assertEquals(LogSeverity.INFO, entry1.getSeverity());
        assertEquals(infoLogTrace1, entry1.getTrace());
        assertEquals(infoLogSourceLocation1, entry1.getSourceLocation());
        assertEquals(infoLogTimestamp1, entry1.getTimestamp());

        assertEquals(LogSeverity.INFO, entry2.getSeverity());
        assertEquals(infoLogTrace2, entry2.getTrace());
        assertEquals(infoLogSourceLocation2, entry2.getSourceLocation());
        assertEquals(infoLogTimestamp2, entry2.getTimestamp());

        assertEquals(LogSeverity.WARNING, entry3.getSeverity());
        assertEquals(warningLogTrace1, entry3.getTrace());
        assertEquals(warningLogSourceLocation1, entry3.getSourceLocation());
        assertEquals(warningLogTimestamp1, entry3.getTimestamp());

        assertEquals(LogSeverity.WARNING, entry4.getSeverity());
        assertEquals(warningLogTrace2, entry4.getTrace());
        assertEquals(warningLogSourceLocation2, entry4.getSourceLocation());
        assertEquals(warningLogTimestamp2, entry4.getTimestamp());

        assertEquals(LogSeverity.ERROR, entry5.getSeverity());
        assertEquals(errorLogTrace, entry5.getTrace());
        assertEquals(errorLogSourceLocation1, entry5.getSourceLocation());
        assertEquals(errorLogTimestamp1, entry5.getTimestamp());

        assertEquals(LogSeverity.ERROR, entry6.getSeverity());
        assertEquals(errorLogTrace, entry6.getTrace());
        assertEquals(errorLogSourceLocation2, entry6.getSourceLocation());
        assertEquals(errorLogTimestamp2, entry6.getTimestamp());
    }

    @Test
    void testExecute_allFieldAreAccessibleToAdmin_shouldSucceed() {
        logoutUser();
        loginAsAdmin();
        String[] paramsForAdmin = {
                Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY, String.valueOf(LogSeverity.INFO),
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases),
        };
        JsonResult actionOutput = getJsonResult(getAction(paramsForAdmin));

        GeneralLogsData generalLogsData = (GeneralLogsData) actionOutput.getOutput();
        List<GeneralLogEntry> logEntries = generalLogsData.getLogEntries();

        assertEquals(6, logEntries.size());

        GeneralLogEntry entry1 = logEntries.get(0);
        GeneralLogEntry entry2 = logEntries.get(1);
        GeneralLogEntry entry3 = logEntries.get(2);
        GeneralLogEntry entry4 = logEntries.get(3);
        GeneralLogEntry entry5 = logEntries.get(4);
        GeneralLogEntry entry6 = logEntries.get(5);

        assertEquals(infoLogJsonPayLoad1, entry1.getDetails());
        assertEquals(infoLogJsonPayLoad2, entry2.getDetails());
        assertEquals(warningLogJsonPayLoad1, entry3.getDetails());
        assertEquals(warningLogJsonPayLoad2, entry4.getDetails());
        assertEquals(errorLogJsonPayLoad1, entry5.getDetails());
        assertEquals(errorLogJsonPayLoad2, entry6.getDetails());
    }

    @Test
    void testExecute_allFieldsAreHiddenFromNonAdminMaintainers_shouldSucceed() {
        logoutUser();
        loginAsMaintainer();
        String[] paramsForMaintainer = {
                Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY, String.valueOf(LogSeverity.INFO),
                Const.ParamsNames.QUERY_LOGS_STARTTIME, String.valueOf(startTimeForSuccessCases),
                Const.ParamsNames.QUERY_LOGS_ENDTIME, String.valueOf(endTimeForSuccessCases),
        };
        JsonResult actionOutput = getJsonResult(getAction(paramsForMaintainer));

        GeneralLogsData generalLogsData = (GeneralLogsData) actionOutput.getOutput();
        List<GeneralLogEntry> logEntries = generalLogsData.getLogEntries();

        GeneralLogEntry entry1 = logEntries.get(0);
        GeneralLogEntry entry2 = logEntries.get(1);
        GeneralLogEntry entry3 = logEntries.get(2);
        GeneralLogEntry entry4 = logEntries.get(3);
        GeneralLogEntry entry5 = logEntries.get(4);
        GeneralLogEntry entry6 = logEntries.get(5);

        RequestLogDetails details1 = (RequestLogDetails) entry1.getDetails();
        FeedbackSessionAuditLogDetails details2 = (FeedbackSessionAuditLogDetails) entry2.getDetails();
        RequestLogDetails details3 = (RequestLogDetails) entry3.getDetails();
        EmailSentLogDetails details4 = (EmailSentLogDetails) entry4.getDetails();
        RequestLogDetails details5 = (RequestLogDetails) entry5.getDetails();
        ExceptionLogDetails details6 = (ExceptionLogDetails) entry6.getDetails();

        assertEquals(6, logEntries.size());

        assertEquals(infoLogJsonPayLoad1.getRequestMethod(), details1.getRequestMethod());
        assertEquals(infoLogJsonPayLoad1.getResponseStatus(), details1.getResponseStatus());
        assertEquals(infoLogJsonPayLoad1.getResponseTime(), details1.getResponseTime());
        assertEquals(infoLogJsonPayLoad1.getActionClass(), details1.getActionClass());
        assertEquals(infoLogJsonPayLoad1.getMessage(), details1.getMessage());
        assertNull(details1.getRequestParams());
        assertNull(details1.getRequestHeaders());
        assertNull(details1.getUserInfo());
        assertNull(entry1.getMessage());

        assertEquals(infoLogJsonPayLoad2.getMessage(), details2.getMessage());
        assertEquals(infoLogJsonPayLoad2.getAccessType(), details2.getAccessType());
        assertEquals(infoLogJsonPayLoad2.getCourseId(), details2.getCourseId());
        assertEquals(infoLogJsonPayLoad2.getFeedbackSessionName(), details2.getFeedbackSessionName());
        assertNull(details2.getStudentEmail());
        assertNull(entry2.getMessage());

        assertEquals(warningLogJsonPayLoad1.getRequestMethod(), details3.getRequestMethod());
        assertEquals(warningLogJsonPayLoad1.getResponseStatus(), details3.getResponseStatus());
        assertEquals(warningLogJsonPayLoad1.getResponseTime(), details3.getResponseTime());
        assertEquals(warningLogJsonPayLoad1.getActionClass(), details3.getActionClass());
        assertEquals(warningLogJsonPayLoad1.getMessage(), details3.getMessage());
        assertNull(details3.getRequestParams());
        assertNull(details3.getRequestHeaders());
        assertNull(details3.getUserInfo());
        assertNull(entry3.getMessage());

        assertEquals(warningLogJsonPayLoad2.getEmailStatusMessage(), details4.getEmailStatusMessage());
        assertEquals(warningLogJsonPayLoad2.getEmailStatus(), details4.getEmailStatus());
        assertEquals(warningLogJsonPayLoad2.getEmailType(), details4.getEmailType());
        assertEquals(warningLogJsonPayLoad2.getMessage(), details4.getMessage());
        assertNull(details4.getEmailRecipient());
        assertNull(details4.getEmailSubject());
        assertNull(details4.getEmailContent());
        assertNull(entry4.getMessage());

        assertEquals(errorLogJsonPayLoad1.getRequestMethod(), details5.getRequestMethod());
        assertEquals(errorLogJsonPayLoad1.getResponseStatus(), details5.getResponseStatus());
        assertEquals(errorLogJsonPayLoad1.getResponseTime(), details5.getResponseTime());
        assertEquals(errorLogJsonPayLoad1.getActionClass(), details5.getActionClass());
        assertEquals(errorLogJsonPayLoad1.getMessage(), details5.getMessage());
        assertNull(details5.getRequestParams());
        assertNull(details5.getRequestHeaders());
        assertNull(details5.getUserInfo());
        assertNull(entry5.getMessage());

        assertEquals(errorLogJsonPayLoad2.getExceptionClass(), details6.getExceptionClass());
        assertEquals(errorLogJsonPayLoad2.getLoggerSourceLocation(), details6.getLoggerSourceLocation());
        assertEquals(errorLogJsonPayLoad2.getExceptionClasses(), details6.getExceptionClasses());
        assertEquals(errorLogJsonPayLoad2.getExceptionStackTraces(), details6.getExceptionStackTraces());
        assertNull(details6.getExceptionMessages());
        assertNull(entry6.getMessage());
    }
}
