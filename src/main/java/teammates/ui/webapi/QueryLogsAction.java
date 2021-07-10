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
 * Queries the logs.
 */
public class QueryLogsAction extends AdminOnlyAction {
    private static final String DEFAULT_SEVERITIES = "INFO";
    private static final int DEFAULT_PAGE_SIZE = 20;

    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 1000L * 60 * 60 * 24;

    private static final List<String> LOG_SEVERITIES = Arrays.stream(LogSeverity.values())
            .map(Enum::toString)
            .collect(Collectors.toList());

    @Override
    ActionResult execute() {
        String severitiesStr = getRequestParamValue(Const.ParamsNames.QUERY_LOGS_SEVERITIES);
        if (severitiesStr == null) {
            severitiesStr = DEFAULT_SEVERITIES;
        }

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

        String nextPageToken = getRequestParamValue(Const.ParamsNames.NEXT_PAGE_TOKEN);

        List<String> severities = parseSeverities(severitiesStr);
        try {
            QueryLogsResults queryResults = logsProcessor.queryLogs(severities, startTime, endTime,
                    DEFAULT_PAGE_SIZE, nextPageToken);
            GeneralLogsData generalLogsData = new GeneralLogsData(queryResults);
            return new JsonResult(generalLogsData);
        } catch (LogServiceException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Parse severities String to a list of severity and check whether each value is
     * a valid LogSeverity value. If it is not a legal LogSeverity value, it will be removed.
     */
    private List<String> parseSeverities(String severitiesStr) {
        List<String> severities = Arrays.asList(severitiesStr.split(","));
        severities.removeIf(severity -> !LOG_SEVERITIES.contains(severity));
        return severities;
    }
}
