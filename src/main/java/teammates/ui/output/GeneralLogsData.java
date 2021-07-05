package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.datatransfer.QueryLogsResults;

/**
 * The API output format for general logs for query action.
 */
public class GeneralLogsData extends ApiOutput {
    private List<GeneralLogEntry> logEntries = new ArrayList<>();
    @Nullable
    private String nextPageToken;

    public GeneralLogsData(List<GeneralLogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    public GeneralLogsData(List<GeneralLogEntry> logEntries, String nextPageToken) {
        this.logEntries = logEntries;
        this.nextPageToken = nextPageToken;
    }

    public GeneralLogsData(QueryLogsResults queryResults) {
        this.logEntries = queryResults.getLogEntries();
        this.nextPageToken = queryResults.getNextPageToken();
    }

    public List<GeneralLogEntry> getLogEntries() {
        return logEntries;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
