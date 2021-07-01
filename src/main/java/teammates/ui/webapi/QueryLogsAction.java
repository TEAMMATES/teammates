package teammates.ui.webapi;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.logging.type.LogSeverity;
import org.apache.http.HttpStatus;
import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.LogServiceException;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
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
        Logger log = Logger.getLogger();
        log.info("query action starts execute!!!!!!!!!");
        String severitiesStr;
        try {
            severitiesStr = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_SEVERITIES);
        } catch (NullHttpParameterException e) {
            severitiesStr = DEFAULT_SEVERITIES;
        }
        List<String> severities = this.parseSeverities(severitiesStr);

        log.info("severity string: " + severitiesStr);

        Instant startTime;
        Instant endTime;
        String nextPageToken;
        try {
            String endTimeStr = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_ENDTIME);
            endTime = Instant.ofEpochMilli(Long.parseLong(endTimeStr));
        } catch (NullHttpParameterException e) {
            endTime = Instant.now();
        }

        try {
            String startTimeStr = getNonNullRequestParamValue(Const.ParamsNames.QUERY_LOGS_STARTTIME);
            startTime = Instant.ofEpochMilli(Long.parseLong(startTimeStr));
        } catch (NullHttpParameterException e) {
            startTime = endTime.minusMillis(TWENTY_FOUR_HOURS_IN_MILLIS);
        }

        try {
            nextPageToken = getNonNullRequestParamValue(Const.ParamsNames.NEXT_PAGE_TOKEN);
        } catch (NullHttpParameterException e) {
            nextPageToken = null;
        }

        if (endTime.toEpochMilli() < startTime.toEpochMilli()) {
            throw new InvalidHttpParameterException("The end time should be after the start time.");
        }

        log.info("startTime: " + startTime.toEpochMilli() + " endTime: " + endTime.toEpochMilli());

        Page<LogEntry> logResults = logsProcessor.queryLogs(severities, startTime, endTime, 20, nextPageToken);

        log.info("result!!!!!!!: ");

        GeneralLogsData generalLogsData = new GeneralLogsData(logResults);

        return new JsonResult(generalLogsData);
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
