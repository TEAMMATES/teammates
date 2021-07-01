package teammates.common.datatransfer;

import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.cloud.logging.SourceLocation;

import org.json.JSONObject;

import javax.annotation.Nullable;

public class GeneralLogEntry {
    private final String logName;
    private final Severity severity;
    private final String trace;
    private final SourceLocation sourceLocation;
    private final Payload<?> payload;
    private final long timestamp;
    @Nullable
    private JSONObject jsonObject;

    public GeneralLogEntry(String logName,
                           Severity severity,
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
                            Severity severity,
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

    public Severity getSeverity() {
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
}
