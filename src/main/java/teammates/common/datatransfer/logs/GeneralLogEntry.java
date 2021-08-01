package teammates.common.datatransfer.logs;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Represents a log entry and contains the fields that are more important
 * for tracing and debugging purposes.
 */
public class GeneralLogEntry {
    private final String logName;
    private final String severity;
    private final String trace;
    private final String insertId;
    private final Map<String, String> resourceIdentifier;
    private final SourceLocation sourceLocation;
    private final long timestamp;
    @Nullable
    private String message;
    @Nullable
    private Map<String, Object> details;

    public GeneralLogEntry(String logName, String severity, String trace, String insertId,
                           Map<String, String> resourceIdentifier, SourceLocation sourceLocation, long timestamp) {
        this.logName = logName;
        this.severity = severity;
        this.trace = trace;
        this.insertId = insertId;
        this.resourceIdentifier = resourceIdentifier;
        this.sourceLocation = sourceLocation;
        this.timestamp = timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public String getLogName() {
        return logName;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTrace() {
        return trace;
    }

    public String getInsertId() {
        return insertId;
    }

    public Map<String, String> getResourceIdentifier() {
        return resourceIdentifier;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

}
