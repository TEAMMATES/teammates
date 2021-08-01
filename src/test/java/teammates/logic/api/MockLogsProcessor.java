package teammates.logic.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.logging.type.LogSeverity;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.SourceLocation;

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
    public void insertErrorLog(String message, String severity, String traceId) {
        errorLogs.add(new ErrorLogEntry(message, severity, traceId));
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
    public void insertInfoLog(String trace, String insertId, SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, Map<String, Object> jsonPayloadDetails) {
        insertGeneralLog(STDOUT_LOG_NAME, SEVERITY_INFO, trace, insertId,
                sourceLocation, timestamp, textPayloadMessage, jsonPayloadDetails);
    }

    /**
     * Simulates insertion of general WARNING logs.
     */
    public void insertWarningLog(String trace, String insertId, SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, Map<String, Object> jsonPayloadDetails) {
        insertGeneralLog(STDERR_LOG_NAME, SEVERITY_WARNING, trace, insertId,
                sourceLocation, timestamp, textPayloadMessage, jsonPayloadDetails);
    }

    /**
     * Simulates insertion of general ERROR logs.
     */
    public void insertGeneralErrorLog(String trace, String insertId, SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, Map<String, Object> jsonPayloadDetails) {
        insertGeneralLog(STDERR_LOG_NAME, SEVERITY_ERROR, trace, insertId,
                sourceLocation, timestamp, textPayloadMessage, jsonPayloadDetails);
    }

    private void insertGeneralLog(String logName, String severity, String trace, String insertId,
            SourceLocation sourceLocation, long timestamp, String textPayloadMessage,
            Map<String, Object> jsonPayloadDetails) {
        GeneralLogEntry logEntry = new GeneralLogEntry(logName, severity, trace, insertId, new HashMap<>(), sourceLocation,
                timestamp);
        logEntry.setMessage(textPayloadMessage);
        logEntry.setDetails(jsonPayloadDetails);
        generalLogs.add(logEntry);
    }

    @Override
    public QueryLogsResults queryLogs(QueryLogsParams queryLogsParams) {
        List<GeneralLogEntry> queryResults = new ArrayList<>();
        if (queryLogsParams.getSeverity() != null) {
            generalLogs.forEach(entry -> {
                if (queryLogsParams.getSeverity().equals(entry.getSeverity())
                        && entry.getTimestamp() >= queryLogsParams.getStartTime()
                        && entry.getTimestamp() <= queryLogsParams.getEndTime()) {
                    queryResults.add(entry);
                }
            });
        } else if (queryLogsParams.getMinSeverity() != null) {
            generalLogs.forEach(entry -> {
                if (LogSeverity.valueOf(queryLogsParams.getMinSeverity()).getNumber()
                        <= LogSeverity.valueOf(entry.getSeverity()).getNumber()
                        && entry.getTimestamp() >= queryLogsParams.getStartTime()
                        && entry.getTimestamp() <= queryLogsParams.getEndTime()) {
                    queryResults.add(entry);
                }
            });
        } else {
            generalLogs.forEach(entry -> {
                if (entry.getTimestamp() >= queryLogsParams.getStartTime()
                        && entry.getTimestamp() <= queryLogsParams.getEndTime()) {
                    queryResults.add(entry);
                }
            });
        }
        return new QueryLogsResults(queryResults, false);
    }

    @Override
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) {
        // No-op
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            long startTime, long endTime, String fsName) {
        return feedbackSessionLogs;
    }

}
