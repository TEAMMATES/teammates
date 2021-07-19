package teammates.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.logging.type.LogSeverity;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.datatransfer.QueryLogsParams;
import teammates.common.datatransfer.QueryLogsParams.UserInfoParams;
import teammates.common.datatransfer.QueryLogsResults;
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
    private List<GeneralLogEntry> generalLogs = new ArrayList<>();

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

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        return errorLogs;
    }

    /**
     * Simulates insertion of general INFO logs.
     */
    public void insertInfoLog(String trace, GeneralLogEntry.SourceLocation sourceLocation, long timestamp,
            String textPayloadMessage) {
        insertGeneralLogWithTextPayload(STDOUT_LOG_NAME, SEVERITY_INFO, trace, sourceLocation, timestamp,
                textPayloadMessage);
    }

    /**
     * Simulates insertion of general WARNING logs.
     */
    public void insertWarningLog(String trace, GeneralLogEntry.SourceLocation sourceLocation, long timestamp,
            String textPayloadMessage) {
        insertGeneralLogWithTextPayload(STDERR_LOG_NAME, SEVERITY_WARNING, trace, sourceLocation, timestamp,
                textPayloadMessage);
    }

    /**
     * Simulates insertion of general ERROR logs.
     */
    public void insertGeneralErrorLog(String trace, GeneralLogEntry.SourceLocation sourceLocation, long timestamp,
            String textPayloadMessage) {
        insertGeneralLogWithTextPayload(STDERR_LOG_NAME, SEVERITY_ERROR, trace, sourceLocation, timestamp,
                textPayloadMessage);
    }

    private void insertGeneralLogWithTextPayload(String logName, String severity, String trace,
            GeneralLogEntry.SourceLocation sourceLocation, long timestamp, String textPayloadMessage) {
        GeneralLogEntry logEntry = new GeneralLogEntry(logName, severity, trace, sourceLocation, timestamp);
        logEntry.setMessage(textPayloadMessage);
        generalLogs.add(logEntry);
    }

    @Override
    public QueryLogsResults queryLogs(QueryLogsParams queryLogsParams) {
        List<GeneralLogEntry> queryResults = new ArrayList<>();
        if (queryLogsParams.getSeverityLevel() != null) {
            generalLogs.forEach(entry -> {
                if (queryLogsParams.getSeverityLevel().equals(entry.getSeverity())
                        && entry.getTimestamp() >= queryLogsParams.getStartTime().toEpochMilli()
                        && entry.getTimestamp() <= queryLogsParams.getEndTime().toEpochMilli()) {
                    queryResults.add(entry);
                }
            });
        } else if (queryLogsParams.getMinSeverity() != null) {
            generalLogs.forEach(entry -> {
                if (LogSeverity.valueOf(queryLogsParams.getMinSeverity()).getNumber()
                        <= LogSeverity.valueOf(entry.getSeverity()).getNumber()
                        && entry.getTimestamp() >= queryLogsParams.getStartTime().toEpochMilli()
                        && entry.getTimestamp() <= queryLogsParams.getEndTime().toEpochMilli()) {
                    queryResults.add(entry);
                }
            });
        } else {
            generalLogs.forEach(entry -> {
                if (entry.getTimestamp() >= queryLogsParams.getStartTime().toEpochMilli()
                        && entry.getTimestamp() <= queryLogsParams.getEndTime().toEpochMilli()) {
                    queryResults.add(entry);
                }
            });
        }
        return new QueryLogsResults(queryResults, null);
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

}
