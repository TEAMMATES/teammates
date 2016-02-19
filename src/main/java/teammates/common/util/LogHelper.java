package teammates.common.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

public class LogHelper {
    /**
     * 6 past versions to query, including the current version and its 5 preceding versions.
     */
    public static final int MAX_PAST_VERSIONS_TO_QUERY = 6;
    
    private LogQuery query;
    
    public LogHelper() {
        query = LogQuery.Builder.withDefaults();
    }
    
    /**
     * Sets values for query.
     * If versionsToQuery is null or empty, the current version with its 5 preceding versions will be used instead.
     * 
     * @param versionsToQuery 
     * @param startTime
     * @param endTime
     * @param offset
     */
    public void setQuery(List<String> versionsToQuery, Long startTime, Long endTime, String offset) {
        query.includeAppLogs(true);
        query.batchSize(1000);
        query.minLogLevel(LogLevel.INFO);
        if (startTime != null) {
            query.startTimeMillis(startTime);
        }
        if (endTime != null) {
            query.endTimeMillis(endTime);
        }
        query.majorVersionIds(getVersionIdsForQuery(versionsToQuery));
        if (offset != null && !offset.equals("null")) {
            query.offset(offset);
        }
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
        ModulesService modulesService = ModulesServiceFactory.getModulesService();
        String[] versionList = (String[]) modulesService.getVersions(null).toArray(); // null == default module
        String currentVersion = modulesService.getCurrentVersion();
        int currentVersionIndex = getCurrentVersionIndex(versionList, currentVersion);
        return getNextFewVersions(versionList, currentVersionIndex);
    }

    /**
     * Finds the current version then get at most 5 versions below it.
     * @param currentVersionIndex starting position to get versions to query
     */
    private List<String> getNextFewVersions(String[] versionList, int currentVersionIndex) {
        List<String> result = new ArrayList<String>();
        for(int i = currentVersionIndex; i < versionList.length; i++) {
            result.add(versionList[i]);
            if (result.size() >= MAX_PAST_VERSIONS_TO_QUERY) {
                return result;
            }
        }
        return result;
    }

    /**
     * Finds the index of the current version in the given list.
     * Returns the size of the list if the current version is not found. This is not supposed to happen!
     */
    private int getCurrentVersionIndex(String[] versionList, String currentVersion) {
        for(int i = 0; i < versionList.length; i++) {
            if (versionList[i].equals(currentVersion)) return i;
        }
        return versionList.length;
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
}
