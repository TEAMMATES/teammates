package teammates.common.datatransfer.logs;

/**
 * Severity level for logs.
 */
// CHECKSTYLE.OFF:JavadocVariable enum names are self-documenting
public enum LogSeverity {
    DEFAULT(-1),
    DEBUG(0),
    INFO(1),
    WARNING(2),
    ERROR(3),
    CRITICAL(4);

    private final int severityLevel;

    LogSeverity(int severityLevel) {
        this.severityLevel = severityLevel;
    }

    public int getSeverityLevel() {
        return severityLevel;
    }
}
