package teammates.common.datatransfer;

import java.util.List;

import teammates.common.datatransfer.logs.ExceptionLogDetails;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.util.JsonUtils;

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

    /**
     * Converts a {@link GeneralLogEntry} to a condensed {@link ErrorLogEntry}.
     */
    public static ErrorLogEntry fromLogEntry(GeneralLogEntry logEntry) {
        assert logEntry.getSeverity().getSeverityLevel() >= LogSeverity.ERROR.getSeverityLevel();

        String message;
        if (logEntry.getDetails() == null) {
            message = logEntry.getMessage();
        } else if (logEntry.getDetails().getEvent() != LogEvent.EXCEPTION_LOG) {
            message = JsonUtils.toJson(logEntry.getDetails());
        } else {
            ExceptionLogDetails exceptionLog = (ExceptionLogDetails) logEntry.getDetails();
            StringBuilder sb = new StringBuilder();
            sb.append(exceptionLog.getMessage()).append('\n');

            List<String> exceptionClasses = exceptionLog.getExceptionClasses();
            List<String> exceptionMessages = exceptionLog.getExceptionMessages();
            List<List<String>> exceptionStackTraces = exceptionLog.getExceptionStackTraces();

            // This is a set of extra-defensive checks. A well-formed exception log should not need these checks
            // as exception messages are not null and all the list sizes are equal.
            int numIterations = Math.min(exceptionClasses.size(), exceptionStackTraces.size());
            if (exceptionMessages != null) {
                numIterations = Math.min(numIterations, exceptionMessages.size());
            }

            for (int i = 0; i < numIterations; i++) {
                sb.append("caused by ").append(exceptionClasses.get(i));
                if (exceptionMessages != null) {
                    sb.append(": ").append(exceptionMessages.get(i));
                }
                sb.append('\n');
                List<String> stackTraces = exceptionStackTraces.get(i);
                for (String stackTrace : stackTraces) {
                    sb.append("    at ").append(stackTrace).append('\n');
                }
            }
            message = sb.toString();
        }
        String severity = logEntry.getSeverity().toString();
        String traceId = logEntry.getTrace();
        return new ErrorLogEntry(message, severity, traceId);
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
