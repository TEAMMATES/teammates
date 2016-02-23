package teammates.common.util;

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
    private static final boolean INCLUDE_APP_LOG = true;
    
    /**
     * Affects the internal strategy to get logs. It doesn't affect the result.
     */
    private static final int BATCH_SIZE = 1000;
    private static final LogLevel MIN_LOG_LEVEL = LogLevel.INFO;
    
    private LogQuery query;
    private long endTime;
    
    /**
     * Sets values for query.
     * 
     * @param versionsToQuery 
     * @param startTime
     * @param endTime
     */
    public AdminLogQuery(List<String> versionsToQuery, Long startTime, Long endTime) {
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
     * If endTime is null, it will be considered as the current time.
     * @param startTime
     * @param endTime
     */
    private void setTimePeriod(Long startTime, Long endTime) {
        if (startTime != null) {
            query.startTimeMillis(startTime);
        }
        
        if (endTime == null) {
            endTime = TimeHelper.now(0.0).getTimeInMillis();
        }
        query.endTimeMillis(endTime);
        setEndTime(endTime);
    }
    
    /**
     * Sets the time period to query logs from endTime back to endTime - timeInMillis
     * then moves endTime to right before the query's start time.
     * 
     * @param timeInMillis time period in milliseconds to query logs 
     * starting from endTime back to endTime - timeInMillis
     */
    public void setQueryWindowBackward(long timeInMillis) {
        long startTime = getEndTime() - timeInMillis;
        setTimePeriod(startTime, getEndTime());
        setEndTime(startTime - 1);
    }
    
    /**
     * Sets end time of the query.
     */
    private void setEndTime(Long endTimeParam) {
        endTime = endTimeParam;
    }
    
    /**
     * Gets end time of the query.
     */
    public long getEndTime() {
        return endTime;
    }
}
