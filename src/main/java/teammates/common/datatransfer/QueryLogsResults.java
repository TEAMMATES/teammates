package teammates.common.datatransfer;

import java.util.List;

/**
 * Represents the results of querying logs.
 */
public class QueryLogsResults {
    private final List<GeneralLogEntry> logEntries;
    private final boolean hasNextPage;

    public QueryLogsResults(List<GeneralLogEntry> logEntries, boolean hasNextPage) {
        this.logEntries = logEntries;
        this.hasNextPage = hasNextPage;
    }

    public List<GeneralLogEntry> getLogEntries() {
        return logEntries;
    }

    public boolean getHasNextPage() {
        return hasNextPage;
    }
}
