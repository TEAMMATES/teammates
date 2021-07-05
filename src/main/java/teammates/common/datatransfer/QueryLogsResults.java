package teammates.common.datatransfer;

import java.util.List;

/**
 * This class contains the results of querying logs action.
 */
public class QueryLogsResults {
    private final List<GeneralLogEntry> logEntries;
    private final String nextPageToken;

    public QueryLogsResults(List<GeneralLogEntry> logEntries, String nextPageToken) {
        this.logEntries = logEntries;
        this.nextPageToken = nextPageToken;
    }

    public List<GeneralLogEntry> getLogEntries() {
        return logEntries;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
