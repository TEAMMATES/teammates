package teammates.common.datatransfer;

import java.util.List;

/**
 * Represents the results of querying logs.
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
