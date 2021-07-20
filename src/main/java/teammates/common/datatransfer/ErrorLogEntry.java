package teammates.common.datatransfer;

/**
 * Represents an error level entry from the logs.
 */
public class ErrorLogEntry {
    private final String message;
    private final String severity;

    public ErrorLogEntry(String message, String severity) {
        this.message = message;
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }
}
