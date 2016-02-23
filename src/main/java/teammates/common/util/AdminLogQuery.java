package teammates.common.util;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.InvalidParametersException;

import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService.LogLevel;

/**
 * A wrapper class for LogQuery to retrieve logs from GAE server.
 */
public class AdminLogQuery {
    /**
     * Maximum number of versions to query.
     * The current value will include the current version and its 5 preceding versions.
     */
    private static final int MAX_VERSIONS_TO_QUERY = 6;
    
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
    private Long endTime;
    private List<String> versionList;
    
    /**
     * Default constructor.
     */
    public AdminLogQuery() {
        query = LogQuery.Builder.withDefaults();
        query.includeAppLogs(INCLUDE_APP_LOG);
        query.batchSize(BATCH_SIZE);
        query.minLogLevel(MIN_LOG_LEVEL);
    }
    
    /**
     * Sets values for query.
     * If versionsToQuery is null or empty, default versions will be used instead.
     * 
     * @param versionsToQuery 
     * @param startTime
     * @param endTime
     */
    public AdminLogQuery(List<String> versionsToQuery, Long startTime, Long endTime) {
        this();
        setTimePeriodForQuery(startTime, endTime);
        versionList = getVersionIdsForQuery(versionsToQuery);
        query.majorVersionIds(versionList);
    }
    
    /**
     * Gets query to retrieve logs.
     */
    public LogQuery getQuery() {
        return query;
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
            setEndTime(endTime);
        }
    }
    
    public void setQueryWindowBackward(long timeInMillis) {
        if (getEndTime() == null) {
            setEndTime(TimeHelper.now(0.0).getTimeInMillis());
        }
        Long startTime = getEndTime() - timeInMillis;
        setTimePeriodForQuery(startTime, getEndTime());
        endTime = startTime - 1;
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
        GaeVersionApi adminApi = new GaeVersionApi();
        List<Version> versionList = adminApi.getAvailableVersions();
        Version currentVersion = adminApi.getCurrentVersion();
        
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
     * Finds at most MAX_VERSIONS_TO_QUERY nearest versions.
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
}
