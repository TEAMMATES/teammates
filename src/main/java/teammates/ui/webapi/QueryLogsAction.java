package teammates.ui.webapi;

import java.time.Instant;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.datatransfer.GeneralLogEntry.SourceLocation;
import teammates.common.datatransfer.QueryLogsParams;
import teammates.common.datatransfer.QueryLogsParams.UserInfoParams;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.LogServiceException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.LogEvent;
import teammates.ui.output.GeneralLogsData;

/**
 * Queries the logs.
 */
public class QueryLogsAction extends AdminOnlyAction {
    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 1000L * 60 * 60 * 24;

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isMaintainer && !userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Only Maintainers or Admin are allowed to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        Instant endTime = Instant.now();
        try {
            String endTimeStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ENDTIME);
            if (endTimeStr != null) {
                endTime = Instant.ofEpochMilli(Long.parseLong(endTimeStr));
            }
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid end time.", e);
        }

        Instant startTime = endTime.minusMillis(TWENTY_FOUR_HOURS_IN_MILLIS);
        try {
            String startTimeStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_STARTTIME);
            if (startTimeStr != null) {
                startTime = Instant.ofEpochMilli(Long.parseLong(startTimeStr));
            }
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid start time.", e);
        }

        if (endTime.toEpochMilli() < startTime.toEpochMilli()) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        String severity = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SEVERITY);
        String minSeverity = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY);
        String nextPageToken = getRequestParamValue(Const.ParamsNames.NEXT_PAGE_TOKEN);
        String traceId = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_TRACE);
        String actionClass = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ACTION_CLASS);
        String logEvent = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EVENT);
        String sourceLocationFile = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SOURCE_LOCATION_FILE);
        String sourceLocationFunction = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SOURCE_LOCATION_FUNCTION);
        String exceptionClass = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EXCEPTION_CLASS);
        String order = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ORDER);
        String googleId = null;
        String regkey = null;
        String email = null;

        if (userInfo.isAdmin) {
            googleId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);
            regkey = getRequestParamValue(Const.ParamsNames.REGKEY);
            email = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EMAIL);
        }

        QueryLogsParams queryLogsParams = QueryLogsParams.builder(startTime, endTime)
                .withSeverityLevel(severity)
                .withMinSeverity(minSeverity)
                .withTraceId(traceId)
                .withActionClass(actionClass)
                .withUserInfo(new UserInfoParams(googleId, regkey, email))
                .withLogEvent(logEvent)
                .withSourceLocation(new SourceLocation(sourceLocationFile, null, sourceLocationFunction))
                .withExceptionClass(exceptionClass)
                .withOrder(order)
                .withPageSize(DEFAULT_PAGE_SIZE)
                .withPageToken(nextPageToken)
                .build();
        try {
            QueryLogsResults queryResults = logsProcessor.queryLogs(queryLogsParams);
            removeSensitiveFields(queryResults);
            GeneralLogsData generalLogsData = new GeneralLogsData(queryResults);
            return new JsonResult(generalLogsData);
        } catch (LogServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void removeSensitiveFields(QueryLogsResults queryResults) {
        if (userInfo.isAdmin) {
            return;
        }

        for (GeneralLogEntry logEntry : queryResults.getLogEntries()) {
            if (logEntry.getDetails() != null) {
                Map<String, Object> details = logEntry.getDetails();
                // Always remove requestParams, requestHeaders and userInfo for non-admin maintainers
                details.remove("requestParams");
                details.remove("requestHeaders");
                details.remove("userInfo");
                // Keep log message of event logs and remove log message for other logs
                if (!details.containsKey("event")) {
                    details.remove("message");
                }
                // Remove email details in email sent event log
                if (LogEvent.EMAIL_SENT.toString().equals(details.get("event"))) {
                    details.remove("emailDetails");
                }
                // Remove student email in feedback session audit event log
                if (LogEvent.FEEDBACK_SESSION_AUDIT.toString().equals(details.get("event"))) {
                    details.remove("studentEmail");
                }
            }
            // Always remove text payload message for non-admin maintainers
            logEntry.setMessage(null);
        }
    }
}
