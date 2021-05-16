package teammates.common.datatransfer;

public class ErrorLogEntry {
    public String message;
    public String severity;

    public ErrorLogEntry(String message, String severity) {
        this.message = message;
        this.severity = severity;
    }
}
