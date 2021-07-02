package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

public class QueryResults {
    private List<GeneralLogEntry> logEntries = new ArrayList<>();
    private String nextPageToken;

    /**
     * Empty constructor is used for testing.
     */
    public QueryResults() {

    }

    public QueryResults(List<GeneralLogEntry> logEntries, String nextPageToken) {
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
