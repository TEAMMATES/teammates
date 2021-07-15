package teammates.ui.webapi;

import java.time.Instant;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.GeneralLogEntry.SourceLocation;
import teammates.common.datatransfer.QueryLogsParams;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Const;
import teammates.ui.output.GeneralLogsData;

/**
 * Queries the logs.
 */
public class QueryLogsAction extends AdminOnlyAction {
    private static final int DEFAULT_PAGE_SIZE = 20;

    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 1000L * 60 * 60 * 24;

    @Override
    ActionResult execute() {

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
        String googleId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);
        String regkey = getRequestParamValue(Const.ParamsNames.REGKEY);
        String email = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EMAIL);
        String logEvent = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EVENT);
        String sourceLocationFile = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SOURCE_LOCATION_FILE);
        String sourceLocationFunction = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SOURCE_LOCATION_FUNCTION);
        String exceptionClass = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_EXCEPTION_CLASS);

        QueryLogsParams queryLogsParams = new QueryLogsParams(severity, minSeverity, startTime, endTime,
                traceId, actionClass, googleId, regkey, email, logEvent,
                new SourceLocation(sourceLocationFile, null, sourceLocationFunction), exceptionClass,
                DEFAULT_PAGE_SIZE, nextPageToken);
        try {
            QueryLogsResults queryResults = logsProcessor.queryLogs(queryLogsParams);
            GeneralLogsData generalLogsData = new GeneralLogsData(queryResults);
            return new JsonResult(generalLogsData);
        } catch (LogServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
