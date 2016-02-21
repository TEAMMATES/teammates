package teammates.common.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teammates.common.exception.InvalidParametersException;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;
import com.google.appengine.api.log.LogService.LogLevel;

public class LogHelper {
    /**
     * 6 versions to query, including the current version and its 5 preceding versions.
     */
    private static final int MAX_VERSIONS_TO_QUERY = 6;
    
    /**
     * Always includes application logs
     */
    private static final boolean INCLUDE_APP_LOG = true;
    
    /**
     * Affects the internal strategy to get logs. It doesn't affect the result.
     */
    private static final int BATCH_SIZE = 1000;
    private static final LogLevel MIN_LOG_LEVEL = LogLevel.INFO;
    public static final int SEARCH_TIME_INCREMENT = 2*60*60*1000;  // two hours in millisecond
    
    private LogQuery query;
    private Long endTime;
    private List<String> versionList;
    
    public LogHelper() {
        query = LogQuery.Builder.withDefaults();
        query.includeAppLogs(INCLUDE_APP_LOG);
        query.batchSize(BATCH_SIZE);
        query.minLogLevel(MIN_LOG_LEVEL);
    }
    
    /**
     * Sets values for query.
     * If versionsToQuery is null or empty, the current version with its 5 preceding versions will be used instead.
     * 
     * @param versionsToQuery 
     * @param startTime
     * @param endTime
     */
    public void setQuery(List<String> versionsToQuery, Long startTime, Long endTime) {
        setTimePeriodForQuery(startTime, endTime);
        setEndTime(endTime);
        versionList = getVersionIdsForQuery(versionsToQuery);
        query.majorVersionIds(versionList);
    }
    
    /**
     * Sets time period to search for query.
     * @param startTime
     * @param endTime
     */
    private void setTimePeriodForQuery(Long startTime, Long endTime) {
        if (startTime != null) {
            query.startTimeMillis(startTime);
        }
        if (endTime != null) {
            query.endTimeMillis(endTime);
        }
    }
    
    /**
     * Sets end time of the query.
     */
    private void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
    
    /**
     * Gets end time of the query.
     */
    public Long getEndTime() {
        return endTime;
    }
    
    /**
     * Gets versions used in query.
     */
    public List<String> getVersionsToQuery() {
        return versionList;
    }
    
    /**
     * Selects versions for query. If versions are not specified, it will return 
     * default versions used for query.
     */
    private List<String> getVersionIdsForQuery(List<String> versions) {
        boolean isVersionSpecifiedInRequest = (versions != null && !versions.isEmpty());
        if (isVersionSpecifiedInRequest) {
            return versions;
        }
        return getDefaultVersionIdsForQuery();
    }
    
    /**
     * Gets a list of versions, including the current version and 5 preceding versions (if available).
     * @return a list of default versions for query.
     */
    private List<String> getDefaultVersionIdsForQuery() {
        List<Version> versionList = Version.getAvailableVersions();
        Version currentVersion = Version.getCurrentVersion();
        
        List<String> defaultVersions = new ArrayList<String>();
        try {
            int currentVersionIndex = getCurrentVersionIndex(versionList, currentVersion);
            defaultVersions = getNextFewVersions(versionList, currentVersionIndex);
        } catch (InvalidParametersException e) {
            defaultVersions.add(currentVersion.toStringForQuery());
            Utils.getLogger().severe(e.getMessage());
        }
        return defaultVersions;
    }

    /**
     * Finds the current version then get at most 5 versions below it.
     * @param currentVersionIndex starting position to get versions to query
     */
    private List<String> getNextFewVersions(List<Version> versionList, int currentVersionIndex) {
        int endIndex = Math.min(currentVersionIndex + MAX_VERSIONS_TO_QUERY, versionList.size());
        List<Version> versionSubList = versionList.subList(currentVersionIndex, endIndex);
        List<String> versionListInString = new ArrayList<String>();
        for(Version version : versionSubList) {
            versionListInString.add(version.toStringForQuery());
        }
        return versionListInString;
    }

    /**
     * Finds the index of the current version in the given list.
     * @throws InvalidParametersException when the current version is not found
     */
    private int getCurrentVersionIndex(List<Version> versionList, Version currentVersion) 
                    throws InvalidParametersException {
        int versionIndex = versionList.indexOf(currentVersion);
        if (versionIndex != -1) {
            return versionIndex;
        }
        throw new InvalidParametersException("The current version is not found!");
    }
    
    /**
     * Retrieves logs using the query.
     * @return logs fetched from server.
     */
    public List<AppLogLine> fetchLogs() {
        List<AppLogLine> logs = new LinkedList<AppLogLine>();
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query);
        for (RequestLogs record : records) {
            //fetch application log
            List<AppLogLine> appLogLines = record.getAppLogLines();
            logs.addAll(appLogLines);
        }
        return logs;
    }
    
    /**
     * Retrieves all logs within 2 hours before the endTime.
     * We can use it again to get logs from the next 2 hours.
     * @return logs within 2 hours before endTime
     */
    public List<AppLogLine> fetchLogsInNextHours() {
        List<AppLogLine> logs = new LinkedList<AppLogLine>();
        
        if (endTime == null) {
            setEndTime(TimeHelper.now(0.0).getTimeInMillis());
        }
        Long startTime = endTime - SEARCH_TIME_INCREMENT;
        this.setTimePeriodForQuery(startTime, endTime);
        
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query);
        for (RequestLogs record : records) {
            record.getOffset();
            List<AppLogLine> appLogLines = record.getAppLogLines();
            logs.addAll(appLogLines);
        }
        setEndTime(startTime - 1);
        return logs;
    }
}
