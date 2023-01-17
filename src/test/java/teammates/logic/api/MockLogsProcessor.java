package teammates.logic.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogDetails;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.SourceLocation;
import teammates.logic.external.LocalLoggingService;
import teammates.logic.external.LogService;

/**
 * Allows mocking of {@link LogsProcessor}.
 */
public class MockLogsProcessor extends LogsProcessor {

    private final LogService service = new LocalLoggingService();

    private List<GeneralLogEntry> generalLogs = new ArrayList<>();

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
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) {
        service.createFeedbackSessionLog(courseId, email, fsName, fslType);
    }

    /**
     * Mocks getting feedback session logs.
     */
    @Override
    public List<FeedbackSessionLogEntryAttributes> getFeedbackSessionLogs(String courseId, String email,
            long startTime, long endTime, String fsName) {
        return service.getFeedbackSessionLogs(courseId, email, startTime, endTime, fsName);
    }

}
