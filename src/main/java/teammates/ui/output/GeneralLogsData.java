package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.datatransfer.QueryLogsResults;

/**
 * The API output format for general logs for query action.
 */
public class GeneralLogsData extends ApiOutput {
    private List<GeneralLogEntry> logEntries = new ArrayList<>();
    private boolean hasNextPage;

    public GeneralLogsData(List<GeneralLogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    public GeneralLogsData(List<GeneralLogEntry> logEntries, boolean hasNextPage) {
        this.logEntries = logEntries;
        this.hasNextPage = hasNextPage;
    }

    public GeneralLogsData(QueryLogsResults queryResults) {
        this.logEntries = queryResults.getLogEntries();
        this.hasNextPage = queryResults.getHasNextPage();
    }

    public List<GeneralLogEntry> getLogEntries() {
        return logEntries;
    }

    public boolean getHasNextPage() {
        return hasNextPage;
    }
}
