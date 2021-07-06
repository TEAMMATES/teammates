package teammates.ui.webapi;

import java.time.Instant;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Const;
import teammates.ui.output.GeneralLogsData;

/**
 * Gets the list of error logs for a user-specified period of time.
 */
public class QueryLogsAction extends AdminOnlyAction {
    private static final int DEFAULT_PAGE_SIZE = 20;

    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 1000L * 60 * 60 * 24;

    @Override
    ActionResult execute() {

        String severity = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SEVERITY);
        String minSeverity = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_MIN_SEVERITY);

        Instant endTime;
        try {
            String endTimeStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ENDTIME);
            if (endTimeStr == null) {
                endTime = Instant.now();
            } else {
                endTime = Instant.ofEpochMilli(Long.parseLong(endTimeStr));
            }
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid end time.", e);
        }

        Instant startTime;
        try {
            String startTimeStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_STARTTIME);
            if (startTimeStr == null) {
                startTime = endTime.minusMillis(TWENTY_FOUR_HOURS_IN_MILLIS);
            } else {
                startTime = Instant.ofEpochMilli(Long.parseLong(startTimeStr));
            }
        } catch (NumberFormatException e) {
            throw new InvalidHttpParameterException("Invalid start time.", e);
        }

        if (endTime.toEpochMilli() < startTime.toEpochMilli()) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        String nextPageToken = getRequestParamValue(Const.ParamsNames.NEXT_PAGE_TOKEN);

        try {
            QueryLogsResults queryResults = logsProcessor.queryLogs(severity, minSeverity, startTime, endTime,
                    DEFAULT_PAGE_SIZE, nextPageToken);
            GeneralLogsData generalLogsData = new GeneralLogsData(queryResults);
            return new JsonResult(generalLogsData);
        } catch (LogServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
