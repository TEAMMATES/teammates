package teammates.common.datatransfer;

import java.util.Objects;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.google.cloud.logging.Payload;

/**
 * This class represents a log entry of Google Cloud Logging and contains some of the fields
 * that are more important for querying logs action and are of more interest to maintainers.
 */
public class GeneralLogEntry {
    private final String logName;
    private final String severity;
    private final String trace;
    private final SourceLocation sourceLocation;
    private final Payload<?> payload;
    private final long timestamp;
    @Nullable
    private JSONObject jsonObject;

    public GeneralLogEntry(String logName, String severity, String trace, SourceLocation sourceLocation,
                           Payload<?> payload, long timestamp) {
        this.logName = logName;
        this.severity = severity;
        this.trace = trace;
        this.sourceLocation = sourceLocation;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public GeneralLogEntry(String logName, String severity, String trace, SourceLocation sourceLocation,
                            Payload<?> payload, long timestamp, JSONObject jsonObject) {
        this.logName = logName;
        this.severity = severity;
        this.trace = trace;
        this.sourceLocation = sourceLocation;
        this.payload = payload;
        this.timestamp = timestamp;
        this.jsonObject = jsonObject;
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

    public Payload<?> getPayload() {
        return payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public static class SourceLocation {
        private final String file;
        private final long line;
        private final String function;

        public SourceLocation(String file, long line, String function) {
            this.file = file;
            this.line = line;
            this.function = function;
        }

        public String getFile() {
            return file;
        }

        public long getLine() {
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
                SourceLocation other = (SourceLocation) obj;
                return file.equals(other.getFile())
                        && line == other.getLine()
                        && function.equals(other.getFunction());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(file, line, function);
        }
    }
}
