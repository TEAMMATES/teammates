package teammates.common.util;

import java.util.List;

import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService.LogLevel;

import teammates.common.util.Assumption;

/**
 * A wrapper class for LogQuery to retrieve logs from GAE server.
 */
public class AdminLogQuery {
    /**
     * A flag to decide whether to include application logs in result or not.
     */
    private static final boolean INCLUDE_APP_LOG = true;
    
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
     * @param startTime
     * @param endTime
     */
    public AdminLogQuery(List<String> versionsToQuery, Long startTime, Long endTime) {
        Assumption.assertNotNull(versionsToQuery);
        
        query = LogQuery.Builder.withDefaults();
        query.includeAppLogs(INCLUDE_APP_LOG);
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
     * @param startTime
     * @param endTime
     */
    public void setTimePeriod(Long startTime, Long endTime) {
        if (startTime != null) {
            startTime = 0l;
        }
        
        if (endTime == null) {
            endTime = TimeHelper.now(0.0).getTimeInMillis();
        }
        query.startTimeMillis(startTime);
        query.endTimeMillis(endTime);
        setStartTime(startTime);
        setEndTime(endTime);
    }
    
    /**
     * Sets end time of the query.
     */
    public void setEndTime(Long endTimeParam) {
        endTime = endTimeParam;
    }
    
    /**
     * Sets start time of the query.
     */
    public void setStartTime(Long startTimeParam) {
        startTime = startTimeParam;
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
}
