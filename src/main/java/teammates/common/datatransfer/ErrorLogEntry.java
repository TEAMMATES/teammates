package teammates.common.datatransfer;

/**
 * Represents an error level entry from the logs.
 */
public class ErrorLogEntry {
    public String message;
    public String severity;

    public ErrorLogEntry(String message, String severity) {
        this.message = message;
        this.severity = severity;
    }
}
