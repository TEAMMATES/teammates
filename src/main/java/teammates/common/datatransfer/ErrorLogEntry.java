package teammates.common.datatransfer;

/**
 * Represents an error level entry from the logs.
 */
public class ErrorLogEntry {
    private final String message;
    private final String severity;
    private final String traceId;

    public ErrorLogEntry(String message, String severity, String traceId) {
        this.message = message;
        this.severity = severity;
        this.traceId = traceId;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTraceId() {
        return traceId;
    }
}
