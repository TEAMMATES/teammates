package teammates.common.util;

import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

/**
 * An utility to fetch logs from GAE server.
 */
public class GaeLogApi {
    public static final int SEARCH_TIME_INCREMENT = 2 * 60 * 60 * 1000;  // two hours in milliseconds
    
    /**
     * Retrieves logs using the query.
     * @return logs fetched from server.
     */
    public List<AppLogLine> fetchLogs(AdminLogQuery query) {
        List<AppLogLine> logs = new LinkedList<AppLogLine>();
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query.getQuery());
        for (RequestLogs record : records) {
            //fetch application log
            List<AppLogLine> appLogLines = record.getAppLogLines();
            logs.addAll(appLogLines);
        }
        return logs;
    }
    
    /**
     * Retrieves all logs within the number of hours defined by SEARCH_TIME_INCREMENT before the endTime.
     * We can use it again to get logs from the next hours.
     * @return logs within the amount of hours defined by SEARCH_TIME_INCREMENT before endTime.
     */
    public List<AppLogLine> fetchLogsInNextHours(AdminLogQuery query) {
        List<AppLogLine> logs = new LinkedList<AppLogLine>();
        
        if (query.getEndTime() == null) {
            query.setEndTime(TimeHelper.now(0.0).getTimeInMillis());
        }
        Long startTime = query.getEndTime() - SEARCH_TIME_INCREMENT;
        query.setTimePeriodForQuery(startTime, query.getEndTime());
        
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query.getQuery());
        for (RequestLogs record : records) {
            record.getOffset();
            List<AppLogLine> appLogLines = record.getAppLogLines();
            logs.addAll(appLogLines);
        }
        query.setEndTime(startTime - 1);
        return logs;
    }
}
