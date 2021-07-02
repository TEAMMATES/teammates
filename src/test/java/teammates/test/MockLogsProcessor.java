package teammates.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryResults;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.api.LogsProcessor;

/**
 * Allows mocking of {@link LogsProcessor}.
 */
public class MockLogsProcessor extends LogsProcessor {
    private static final String STDOUT_LOG_NAME = "stdout";
    private static final String STDERR_LOG_NAME = "stderr";
    private static final String SEVERITY_INFO = "INFO";
    private static final String SEVERITY_WARNING = "WARNING";
    private static final String SEVERITY_ERROR = "ERROR";

    private List<ErrorLogEntry> errorLogs = new ArrayList<>();
    private List<FeedbackSessionLogEntry> feedbackSessionLogs = new ArrayList<>();
    private List<MockGeneralLogEntry> generalLogs = new ArrayList<>();

    /**
     * Simulates insertion of error logs.
     */
    public void insertErrorLog(String message, String severity) {
        errorLogs.add(new ErrorLogEntry(message, severity));
    }

    /**
     * Simulates insertion of feedback session logs.
     */
    public void insertFeedbackSessionLog(StudentAttributes student, FeedbackSessionAttributes fs,
            String fslType, long timestamp) {
        feedbackSessionLogs.add(new FeedbackSessionLogEntry(student, fs, fslType, timestamp));
    }

    public void insertInfoLog(String trace, String sourceLocation, String payload, long timestamp) {
        insertGeneralLog(STDOUT_LOG_NAME, SEVERITY_INFO, trace, sourceLocation, payload, timestamp);
    }

    public void insertWarningLog(String trace, String sourceLocation, String payload, long timestamp) {
        insertGeneralLog(STDERR_LOG_NAME, SEVERITY_WARNING, trace, sourceLocation, payload, timestamp);
    }

    public void insertErrorLog(String trace, String sourceLocation, String payload, long timestamp) {
        insertGeneralLog(STDERR_LOG_NAME, SEVERITY_ERROR, trace, sourceLocation, payload, timestamp);
    }

    /**
     * Simulates insertion of general logs which include INFO, WARNING, ERROR logs and etc.
     */
    private void insertGeneralLog(String logName,
                                 String severity,
                                 String trace,
                                 String sourceLocation,
                                 String payload,
                                 long timestamp) {
        generalLogs.add(new MockGeneralLogEntry(logName, severity, trace, sourceLocation, payload, timestamp));
    }

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        return errorLogs;
    }

    @Override
    public QueryResults queryLogs(List<String> severities, Instant startTime, Instant endTime,
                                  Integer pageSize, String pageToken) {
        List<MockGeneralLogEntry> queryResults = new ArrayList<>();
        this.generalLogs.forEach(entry -> {
            if (severities.contains(entry.severity) &&
                    entry.timestamp >= startTime.toEpochMilli() &&
                    entry.timestamp <= endTime.toEpochMilli()) {
                queryResults.add(entry);
            }
        });
        return new MockQueryResults(queryResults);
    }

    @Override
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) {
        // No-op
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName) {
        return feedbackSessionLogs;
    }

    public static class MockGeneralLogEntry {
        private final String logName;
        private final String severity;
        private final String trace;
        private final String sourceLocation;
        private final String payload;
        private final long timestamp;

        public MockGeneralLogEntry(String logName,
                              String severity,
                              String trace,
                              String sourceLocation,
                              String payload,
                              long timestamp) {
            this.logName = logName;
            this.severity = severity;
            this.trace = trace;
            this.sourceLocation = sourceLocation;
            this.payload = payload;
            this.timestamp = timestamp;
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

        public String getSourceLocation() {
            return sourceLocation;
        }

        public String getPayload() {
            return payload;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    public static class MockQueryResults extends QueryResults {
        private List<MockGeneralLogEntry> mockLogEntries;

        public MockQueryResults(List<MockGeneralLogEntry> logEntries) {
            this.mockLogEntries = logEntries;
        }
    }

}
