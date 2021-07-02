package teammates.common.datatransfer;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.google.cloud.logging.Payload;

public class GeneralLogEntry {
    private final String logName;
    private final String severity;
    private final String trace;
    private final SourceLocation sourceLocation;
    private final Payload<?> payload;
    private final long timestamp;
    @Nullable
    private JSONObject jsonObject;

    public GeneralLogEntry(String logName,
                           String severity,
                           String trace,
                           SourceLocation sourceLocation,
                           Payload<?> payload,
                           long timestamp) {
        this.logName = logName;
        this.severity = severity;
        this.trace = trace;
        this.sourceLocation = sourceLocation;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public GeneralLogEntry(String logName,
                            String severity,
                            String trace,
                            SourceLocation sourceLocation,
                            Payload<?> payload,
                            long timestamp,
                            JSONObject jsonObject) {
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

    @Override
    public String toString() {
        return "{\n"
                + "  LogName: " + logName + ",\n"
                + "  Severity: " + severity + ",\n"
                + "  Trace: " + trace + ",\n"
                + "  SourceLocation: " + sourceLocation + ",\n"
                + "  Payload: " + payload + ",\n"
                + "  Timestamp: " + timestamp + "\n"
                + "}";
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
