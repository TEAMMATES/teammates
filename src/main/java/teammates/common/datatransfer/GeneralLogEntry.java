package teammates.common.datatransfer;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * This class represents a log entry and contains some of the fields that are more important
 * for querying logs action and are of more interest to maintainers.
 */
public class GeneralLogEntry {
    private final String logName;
    private final String severity;
    private final String trace;
    private final SourceLocation sourceLocation;
    private final long timestamp;
    @Nullable
    private String textPayloadMessage;
    @Nullable
    private Map<String, Object> jsonPayloadMap;

    public GeneralLogEntry(String logName, String severity, String trace, SourceLocation sourceLocation,
                           long timestamp, String textPayloadMessage) {
        this.logName = logName;
        this.severity = severity;
        this.trace = trace;
        this.sourceLocation = sourceLocation;
        this.timestamp = timestamp;
        this.textPayloadMessage = textPayloadMessage;
    }

    public GeneralLogEntry(String logName, String severity, String trace, SourceLocation sourceLocation,
                           long timestamp, Map<String, Object> jsonPayloadMap) {
        this.logName = logName;
        this.severity = severity;
        this.trace = trace;
        this.sourceLocation = sourceLocation;
        this.timestamp = timestamp;
        this.jsonPayloadMap = jsonPayloadMap;
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

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTextPayloadMessage() {
        return textPayloadMessage;
    }

    public Map<String, Object> getJsonPayloadMap() {
        return jsonPayloadMap;
    }

    public static class SourceLocation {
        private final String file;
        private final Long line;
        private final String function;

        public SourceLocation(String file, Long line, String function) {
            this.file = file;
            this.line = line;
            this.function = function;
        }

        public String getFile() {
            return file;
        }

        public Long getLine() {
            return line;
        }

        public String getFunction() {
            return function;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof SourceLocation) {
                SourceLocation sourceLocation = (SourceLocation) obj;
                return this.file.equals(sourceLocation.getFile())
                        && this.line.equals(sourceLocation.getLine())
                        && this.function.equals(sourceLocation.getFunction());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return file.hashCode() + line.hashCode() + function.hashCode();
        }
    }
}
