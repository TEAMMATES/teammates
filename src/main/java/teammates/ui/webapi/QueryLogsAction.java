package teammates.ui.webapi;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import com.google.logging.type.LogSeverity;

import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Const;
import teammates.ui.output.GeneralLogsData;

/**
 * Gets the list of error logs for a user-specified period of time.
 */
public class QueryLogsAction extends AdminOnlyAction {
    private static final String DEFAULT_SEVERITIES = "INFO";

    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 1000L * 60 * 60 * 24;

    private static final List<String> LOG_SEVERITIES = Arrays.stream(LogSeverity.values())
            .map(Enum::toString)
            .collect(Collectors.toList());

    @Override
    ActionResult execute() {
        String severitiesStr;

        severitiesStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SEVERITIES);
        if (severitiesStr == null) {
            severitiesStr = DEFAULT_SEVERITIES;
        }

        Instant endTime;
        try {
            String endTimeStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_ENDTIME);
            if (endTimeStr == null) {
                endTime = Instant.now();
            } else {
                endTime = Instant.ofEpochMilli(Long.parseLong(endTimeStr));
            }
        } catch (NumberFormatException e) {
            return new JsonResult("Invalid end time", HttpStatus.SC_BAD_REQUEST);
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
            return new JsonResult("Invalid start time", HttpStatus.SC_BAD_REQUEST);
        }

        if (endTime.toEpochMilli() < startTime.toEpochMilli()) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        String nextPageToken;
        nextPageToken = getRequestParamValue(Const.ParamsNames.NEXT_PAGE_TOKEN);

        List<String> severities = parseSeverities(severitiesStr);
        try {
            QueryLogsResults queryResults = logsProcessor.queryLogs(severities, startTime, endTime, 20, nextPageToken);
            GeneralLogsData generalLogsData = new GeneralLogsData(queryResults);
            return new JsonResult(generalLogsData);
        } catch (LogServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Parse severities String to a list of severity and check whether each value is
     * a legal LogSeverity value. If it is not a legal LogSeverity value, it will be removed.
     */
    private List<String> parseSeverities(String severitiesStr) {
        List<String> severities = Arrays.asList(severitiesStr.split(","));
        severities.removeIf(severity -> !LOG_SEVERITIES.contains(severity));
        return severities;
    }
}
