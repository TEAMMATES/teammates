package teammates.logic.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogDetails;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.SourceLocation;

/**
 * Allows mocking of {@link LogsProcessor}.
 */
public class MockLogsProcessor extends LogsProcessor {

    private List<FeedbackSessionLogEntry> feedbackSessionLogs = new ArrayList<>();
    private List<GeneralLogEntry> generalLogs = new ArrayList<>();

    /**
     * Simulates insertion of feedback session logs.
     */
    public void insertFeedbackSessionLog(String courseId, String studentEmail, String feedbackSessionName,
            String fslType, long timestamp) {
        feedbackSessionLogs
                .add(new FeedbackSessionLogEntry(courseId, studentEmail, feedbackSessionName, fslType, timestamp));
    }

    /**
     * Simulates insertion of feedback session logs.
     */
    public void insertFeedbackSessionLog(String courseId, UUID studentId, UUID feedbackSessionId,
            String fslType, long timestamp) {
        feedbackSessionLogs
                .add(new FeedbackSessionLogEntry(courseId, studentId, feedbackSessionId, fslType, timestamp));
    }

    /**
     * Simulates insertion of general INFO logs.
     */
    public void insertInfoLog(String trace, String insertId, SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, LogDetails logDetails) {
        insertGeneralLog(LogSeverity.INFO, trace, insertId,
                sourceLocation, timestamp, textPayloadMessage, logDetails);
    }

    /**
     * Simulates insertion of general WARNING logs.
     */
    public void insertWarningLog(String trace, String insertId, SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, LogDetails logDetails) {
        insertGeneralLog(LogSeverity.WARNING, trace, insertId,
                sourceLocation, timestamp, textPayloadMessage, logDetails);
    }

    /**
     * Simulates insertion of general ERROR logs.
     */
    public void insertErrorLog(String trace, String insertId, SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, LogDetails logDetails) {
        insertGeneralLog(LogSeverity.ERROR, trace, insertId,
                sourceLocation, timestamp, textPayloadMessage, logDetails);
    }

    private void insertGeneralLog(LogSeverity severity, String trace, String insertId,
            SourceLocation sourceLocation, long timestamp, String textPayloadMessage,
            LogDetails logDetails) {
        GeneralLogEntry logEntry = new GeneralLogEntry(severity, trace, insertId, new HashMap<>(), sourceLocation,
                timestamp);
        logEntry.setMessage(textPayloadMessage);
        logEntry.setDetails(logDetails);
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
                if (queryLogsParams.getMinSeverity().getSeverityLevel()
                        <= entry.getSeverity().getSeverityLevel()
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
    public void createFeedbackSessionLog(String courseId, UUID studentId, UUID fsId, String fslType) {
        // No-op
    }

    @Override
    public List<FeedbackSessionLogEntry> getOrderedFeedbackSessionLogs(String courseId, String email,
            long startTime, long endTime, String fsName) {
        feedbackSessionLogs.sort((x, y) -> x.compareTo(y));
        return feedbackSessionLogs;
    }

}
