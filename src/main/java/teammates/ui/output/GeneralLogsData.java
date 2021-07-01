package teammates.ui.output;

import com.google.api.gax.paging.Page;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import teammates.common.datatransfer.GeneralLogEntry;

import javax.annotation.Nullable;
import java.util.List;

public class GeneralLogsData extends ApiOutput{
    @Nullable
    private List<GeneralLogEntry> logEntries;
    @Nullable
    private String nextPageToken;

    public GeneralLogsData(List<GeneralLogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    public GeneralLogsData(Page<LogEntry> page) {
        for (LogEntry entry : page.iterateAll()) {
            String logName = entry.getLogName();
            Severity severity = entry.getSeverity();
            String trace = entry.getTrace();
            com.google.cloud.logging.SourceLocation sourceLocation = entry.getSourceLocation();
            Payload<?> payload = entry.getPayload();
            long timestamp = entry.getTimestamp();

            GeneralLogEntry logEntry = new GeneralLogEntry(logName, severity, trace, sourceLocation, payload, timestamp);
            this.logEntries.add(logEntry);
        }
        this.nextPageToken = page.getNextPageToken();
    }

    public List<GeneralLogEntry> getLogEntries() {
        return logEntries;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
