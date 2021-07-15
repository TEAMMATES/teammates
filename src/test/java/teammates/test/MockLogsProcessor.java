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
    public void insertInfoLog(String trace, GeneralLogEntry.SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, String actionClass, String googleId, String regkey, String email,
            String logEvent, String exceptionClass) {
        insertGeneralLogWithTextPayload(STDOUT_LOG_NAME, SEVERITY_INFO, trace, sourceLocation, timestamp,
                textPayloadMessage, actionClass, googleId, regkey, email, logEvent, exceptionClass);
    }

    /**
     * Simulates insertion of general WARNING logs.
     */
    public void insertWarningLog(String trace, GeneralLogEntry.SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, String actionClass, String googleId, String regkey, String email,
            String logEvent, String exceptionClass) {
        insertGeneralLogWithTextPayload(STDERR_LOG_NAME, SEVERITY_WARNING, trace, sourceLocation, timestamp,
                textPayloadMessage, actionClass, googleId, regkey, email, logEvent, exceptionClass);
    }

    /**
     * Simulates insertion of general ERROR logs.
     */
    public void insertGeneralErrorLog(String trace, GeneralLogEntry.SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, String actionClass, String googleId, String regkey, String email,
            String logEvent, String exceptionClass) {
        insertGeneralLogWithTextPayload(STDERR_LOG_NAME, SEVERITY_ERROR, trace, sourceLocation, timestamp,
                textPayloadMessage, actionClass, googleId, regkey, email, logEvent, exceptionClass);
    }

    private void insertGeneralLogWithTextPayload(String logName, String severity, String trace,
            GeneralLogEntry.SourceLocation sourceLocation, long timestamp, String textPayloadMessage,
            String actionClass, String googleId, String regkey, String email, String logEvent, String exceptionClass) {
        MockGeneralLogEntry logEntry = new MockGeneralLogEntry(logName, severity, trace, sourceLocation, timestamp,
                actionClass, googleId, regkey, email, logEvent, exceptionClass);
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

    /**
     * Allows mocking of {@link GeneralLogEntry}.
     */
    public static class MockGeneralLogEntry extends GeneralLogEntry {
        private final String actionClass;
        private final UserInfoParams userInfoParams;
        private final String logEvent;
        private final String exceptionClass;

        public MockGeneralLogEntry(String logName, String severity, String trace, SourceLocation sourceLocation,
                long timestamp, String actionClass, String googleId, String regkey, String email, String logEvent,
                String exceptionClass) {
            super(logName, severity, trace, sourceLocation, timestamp);
            this.actionClass = actionClass;
            this.userInfoParams = new UserInfoParams(googleId, regkey, email);
            this.logEvent = logEvent;
            this.exceptionClass = exceptionClass;
        }

        public String getActionClass() {
            return actionClass;
        }

        public UserInfoParams getUserInfo() {
            return userInfoParams;
        }

        public String getLogEvent() {
            return logEvent;
        }

        public String getExceptionClass() {
            return exceptionClass;
        }
    }

}
