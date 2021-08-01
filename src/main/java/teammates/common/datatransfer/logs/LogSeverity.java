package teammates.common.datatransfer.logs;

/**
 * Severity level for logs.
 */
// CHECKSTYLE.OFF:JavadocVariable enum names are self-documenting
public enum LogSeverity {
    INFO(1),
    WARNING(2),
    ERROR(3);

    private final int severityLevel;

    LogSeverity(int severityLevel) {
        this.severityLevel = severityLevel;
    }

    public int getSeverityLevel() {
        return severityLevel;
    }
}
