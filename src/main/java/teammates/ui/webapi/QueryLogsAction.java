package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.datatransfer.logs.SourceLocation;
import teammates.common.util.Const;
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
        long endTime = Instant.now().toEpochMilli();
        try {
            String endTimeStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ENDTIME);
            if (endTimeStr != null) {
                endTime = Long.parseLong(endTimeStr);
            }
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid end time.", e);
        }

        long startTime = Instant.ofEpochMilli(endTime).minusMillis(TWENTY_FOUR_HOURS_IN_MILLIS).toEpochMilli();
        try {
            String startTimeStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_STARTTIME);
            if (startTimeStr != null) {
                startTime = Long.parseLong(startTimeStr);
            }
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid start time.", e);
        }

        if (endTime < startTime) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        String severityStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SEVERITY);
        LogSeverity severity = null;
        if (severityStr != null) {
            try {
                severity = LogSeverity.valueOf(severityStr);
            } catch (IllegalArgumentException e) {
                throw new InvalidHttpParameterException("Invalid log severity.", e);
            }
        }
        String minSeverityStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY);
        LogSeverity minSeverity = null;
        if (minSeverityStr != null) {
            try {
                minSeverity = LogSeverity.valueOf(minSeverityStr);
            } catch (IllegalArgumentException e) {
                throw new InvalidHttpParameterException("Invalid log minimum severity.", e);
            }
        }
        if (severity == null && minSeverity == null) {
            // default to logs with INFO level or higher
            minSeverity = LogSeverity.INFO;
        }
        String traceId = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_TRACE);
        String actionClass = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ACTION_CLASS);
        String logEvent = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EVENT);
        String sourceLocationFile = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SOURCE_LOCATION_FILE);
        String sourceLocationFunction = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SOURCE_LOCATION_FUNCTION);
        String exceptionClass = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EXCEPTION_CLASS);
        String latency = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_LATENCY);
        String status = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_STATUS);
        String version = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_VERSION);
        String order = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ORDER);
        String googleId = null;
        String regkey = null;
        String email = null;
        String extraFilters = null;

        if (userInfo.isAdmin) {
            googleId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);
            regkey = getRequestParamValue(Const.ParamsNames.REGKEY);
            email = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EMAIL);
            extraFilters = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EXTRA_FILTERS);
        }

        RequestLogUser userInfoParams = new RequestLogUser();
        userInfoParams.setGoogleId(googleId);
        userInfoParams.setRegkey(regkey);
        userInfoParams.setEmail(email);

        QueryLogsParams queryLogsParams = QueryLogsParams.builder(startTime, endTime)
                .withSeverityLevel(severity)
                .withMinSeverity(minSeverity)
                .withTraceId(traceId)
                .withActionClass(actionClass)
                .withUserInfo(userInfoParams)
                .withLogEvent(logEvent)
                .withSourceLocation(new SourceLocation(sourceLocationFile, null, sourceLocationFunction))
                .withExceptionClass(exceptionClass)
                .withLatency(latency)
                .withStatus(status)
                .withVersion(version)
                .withExtraFilters(extraFilters)
                .withOrder(order)
                .withPageSize(DEFAULT_PAGE_SIZE)
                .build();

        QueryLogsResults queryResults = logsProcessor.queryLogs(queryLogsParams);
        removeSensitiveFields(queryResults);
        GeneralLogsData generalLogsData = new GeneralLogsData(queryResults);
        return new JsonResult(generalLogsData);
    }

    private void removeSensitiveFields(QueryLogsResults queryResults) {
        if (userInfo.isAdmin) {
            return;
        }

        for (GeneralLogEntry logEntry : queryResults.getLogEntries()) {
            if (logEntry.getDetails() != null) {
                logEntry.getDetails().hideSensitiveInformation();
            }
            // Always remove text payload message for non-admin maintainers
            logEntry.setMessage(null);
        }
    }
}
