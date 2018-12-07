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
    /**
     * Retrieves logs using the query.
     * @return logs fetched from server.
     */
    public List<AppLogLine> fetchLogs(AdminLogQuery query) {
        List<AppLogLine> logs = new LinkedList<>();
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query.getQuery());
        for (RequestLogs record : records) {
            //fetch application log
            List<AppLogLine> appLogLines = record.getAppLogLines();
            logs.addAll(appLogLines);
        }
        return logs;
    }
}
