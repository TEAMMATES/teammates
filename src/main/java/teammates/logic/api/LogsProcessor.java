package teammates.logic.api;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.util.Config;
import teammates.logic.external.GoogleCloudLoggingService;
import teammates.logic.external.LocalLoggingService;
import teammates.logic.external.LogService;

/**
 * Handles operations related to logs reading/writing.
 *
 * <p>Note that while this interface should support writing logs, most of the application/system logs
 * should be written via the standard Logger class.
 */
public class LogsProcessor {

    private static final LogsProcessor instance = new LogsProcessor();
    private final LogService service;

    LogsProcessor() {
        if (Config.IS_DEV_SERVER) {
            service = new LocalLoggingService();
        } else {
            service = new GoogleCloudLoggingService();
        }
    }

    public static LogsProcessor inst() {
        return instance;
    }

    /**
     * Queries and retrieves logs with given parameters.
     */
    public QueryLogsResults queryLogs(QueryLogsParams queryLogsParams) {
        return service.queryLogs(queryLogsParams);
    }

    /**
     * Gets the number of logs for the event type and extra filters.
     */
    public int getNumberOfLogsForEvent(Instant startTime, Instant endTime, LogEvent logEvent, String extraFilters) {
        int total = 0;
        long logStartTime = startTime.toEpochMilli();

        while (true) {
            QueryLogsParams logsParams = QueryLogsParams.builder(logStartTime, endTime.toEpochMilli())
                    .withLogEvent(logEvent.name())
                    .withExtraFilters(extraFilters)
                    .withOrder("asc")
                    .withPageSize(300)
                    .build();
            QueryLogsResults logFetchResults = queryLogs(logsParams);
            List<GeneralLogEntry> logs = logFetchResults.getLogEntries();
            total += logs.size();
            if (logFetchResults.getHasNextPage() && !logs.isEmpty()) {
                logStartTime = logs.get(logs.size() - 1).getTimestamp();
            } else {
                break;
            }
        }
        return total;
    }

}
