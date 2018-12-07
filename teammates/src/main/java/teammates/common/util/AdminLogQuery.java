package teammates.common.util;

import java.time.Instant;
import java.util.List;

import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService.LogLevel;

/**
 * A wrapper class for LogQuery to retrieve logs from GAE server.
 */
public class AdminLogQuery {
    /**
     * A flag to decide whether to include application logs in result or not.
     */
    private static final boolean SHOULD_INCLUDE_APP_LOG = true;

    /**
     * Affects the internal strategy to get logs. It doesn't affect the result.
     */
    private static final int BATCH_SIZE = 1000;
    private static final LogLevel MIN_LOG_LEVEL = LogLevel.INFO;

    private LogQuery query;
    private long startTime;
    private long endTime;

    /**
     * Sets values for query.
     * If startTime is null, it will be considered as 0.
     * If endTime is null, it will be considered as the current time.
     *
     * @param versionsToQuery decide which versions to find logs from.
     */
    public AdminLogQuery(List<String> versionsToQuery, Long startTime, Long endTime) {
        Assumption.assertNotNull(versionsToQuery);

        query = LogQuery.Builder.withDefaults();
        query.includeAppLogs(SHOULD_INCLUDE_APP_LOG);
        query.batchSize(BATCH_SIZE);
        query.minLogLevel(MIN_LOG_LEVEL);
        setTimePeriod(startTime, endTime);
        query.majorVersionIds(versionsToQuery);
    }

    /**
     * Gets query to retrieve logs.
     */
    public LogQuery getQuery() {
        return query;
    }

    /**
     * Sets time period to search for query.
     * If startTime is null, it will be considered as 0.
     * If endTime is null, it will be considered as the current time.
     *
     * <p>The time is in Unix time. https://en.wikipedia.org/wiki/Unix_time
     * 0 means it will take logs after Thursday, 1 January 1970 (since forever).
     */
    public void setTimePeriod(Long startTimeParam, Long endTimeParam) {
        long startTime = startTimeParam == null ? 0L : startTimeParam;
        long endTime = endTimeParam == null ? Instant.now().toEpochMilli() : endTimeParam;
        query.startTimeMillis(startTime);
        query.endTimeMillis(endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Gets end time of the query.
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Gets start time of the query.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Moves the time period to query logs to the next period in the past with a length of timeInMillis.
     * @param timeInMillis the length of the next period in milliseconds.
     */
    public void moveTimePeriodBackward(long timeInMillis) {
        long nextEndTime = getStartTime() - 1;
        long nextStartTime = nextEndTime - timeInMillis;
        setTimePeriod(nextStartTime, nextEndTime);
    }
}
