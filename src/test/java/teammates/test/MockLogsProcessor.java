package teammates.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.google.logging.type.LogSeverity;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.datatransfer.GeneralLogEntry.SourceLocation;
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
            long timestamp, String textPayloadMessage, String apiEndpoint, String googleId, String regkey, String email,
            String logEvent, String exceptionClass) {
        insertGeneralLogWithTextPayload(STDOUT_LOG_NAME, SEVERITY_INFO, trace, sourceLocation, timestamp,
                textPayloadMessage, apiEndpoint, googleId, regkey, email, logEvent, exceptionClass);
    }

    /**
     * Simulates insertion of general WARNING logs.
     */
    public void insertWarningLog(String trace, GeneralLogEntry.SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, String apiEndpoint, String googleId, String regkey, String email,
            String logEvent, String exceptionClass) {
        insertGeneralLogWithTextPayload(STDERR_LOG_NAME, SEVERITY_WARNING, trace, sourceLocation, timestamp,
                textPayloadMessage, apiEndpoint, googleId, regkey, email, logEvent, exceptionClass);
    }

    /**
     * Simulates insertion of general ERROR logs.
     */
    public void insertGeneralErrorLog(String trace, GeneralLogEntry.SourceLocation sourceLocation,
            long timestamp, String textPayloadMessage, String apiEndpoint, String googleId, String regkey, String email,
            String logEvent, String exceptionClass) {
        insertGeneralLogWithTextPayload(STDERR_LOG_NAME, SEVERITY_ERROR, trace, sourceLocation, timestamp,
                textPayloadMessage, apiEndpoint, googleId, regkey, email, logEvent, exceptionClass);
    }

    private void insertGeneralLogWithTextPayload(String logName, String severity, String trace,
            GeneralLogEntry.SourceLocation sourceLocation, long timestamp, String textPayloadMessage,
            String apiEndpoint, String googleId, String regkey, String email, String logEvent, String exceptionClass) {
        MockGeneralLogEntry logEntry = new MockGeneralLogEntry(logName, severity, trace, sourceLocation, timestamp,
                apiEndpoint, googleId, regkey, email, logEvent, exceptionClass);
        logEntry.setMessage(textPayloadMessage);
        generalLogs.add(logEntry);
    }

    @Override
    public QueryLogsResults queryLogs(String severity, String minSeverity, Instant startTime, Instant endTime,
            String trace, String apiEndpoint, String googleId, String regkey, String email, String logEvent,
            SourceLocation sourceLocation, String exceptionClass, Integer pageSize, String pageToken) {

        Predicate<MockGeneralLogEntry> filterPredicate = getPredicate(severity, minSeverity, startTime, endTime, trace,
                apiEndpoint, googleId, regkey, email, logEvent, sourceLocation, exceptionClass);

        List<GeneralLogEntry> queryResults = new ArrayList<>();
        generalLogs.forEach(logEntry -> {
            if (filterPredicate.test((MockGeneralLogEntry) logEntry)) {
                queryResults.add(logEntry);
            }
        });
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

    private Predicate<MockGeneralLogEntry> getPredicate(String severity, String minSeverity, Instant startTime,
            Instant endTime, String trace, String apiEndpoint, String googleId, String regkey, String email,
            String logEvent, SourceLocation sourceLocation, String exceptionClass) {
        assert startTime != null && endTime != null;
        return logEntry -> {
            boolean matchSeverity = true;
            boolean matchTimePeriod = startTime.toEpochMilli() <= logEntry.getTimestamp()
                    && logEntry.getTimestamp() <= endTime.toEpochMilli();
            boolean matchTrace = true;
            boolean matchApiEndpoint = true;
            boolean matchGoogleId = true;
            boolean matchRegkey = true;
            boolean matchEmail = true;
            boolean matchLogEvent = true;
            boolean matchSourceLocation = true;
            boolean matchExceptionClass = true;

            if (severity != null) {
                matchSeverity = logEntry.getSeverity().equals(severity);
            } else if (minSeverity != null) {
                matchSeverity = LogSeverity.valueOf(minSeverity).getNumber()
                        <= LogSeverity.valueOf(logEntry.getSeverity()).getNumber();
            }
            if (trace != null) {
                matchTrace = logEntry.getTrace().equals(trace);
            }
            if (apiEndpoint != null) {
                matchApiEndpoint = logEntry.getApiEndpoint() != null && logEntry.getApiEndpoint().equals(apiEndpoint);
            }
            if (googleId != null) {
                matchGoogleId = logEntry.getUserInfo() != null && logEntry.getUserInfo().googleId != null
                        && logEntry.getUserInfo().googleId.equals(googleId);
            }
            if (regkey != null) {
                matchRegkey = logEntry.getUserInfo() != null && logEntry.getUserInfo().regkey != null
                        && logEntry.getUserInfo().regkey.equals(regkey);
            }
            if (email != null) {
                matchEmail = logEntry.getUserInfo() != null && logEntry.getUserInfo().email != null
                        && logEntry.getUserInfo().email.equals(email);
            }
            if (logEvent != null) {
                matchLogEvent = logEntry.getLogEvent() != null && logEntry.getLogEvent().equals(logEvent);
            }
            if (sourceLocation != null && sourceLocation.getFile() != null) {
                matchSourceLocation = logEntry.getSourceLocation() != null
                        && logEntry.getSourceLocation().getFile() != null
                        && logEntry.getSourceLocation().getFile().equals(sourceLocation.getFile());
                if (sourceLocation.getFunction() != null) {
                    matchSourceLocation = matchSourceLocation && logEntry.getSourceLocation().getFunction() != null
                            && logEntry.getSourceLocation().getFunction().equals(sourceLocation.getFunction());
                }
            }
            if (exceptionClass != null) {
                matchExceptionClass = logEntry.getExceptionClass() != null
                        && logEntry.getExceptionClass().equals(exceptionClass);
            }
            return matchSeverity && matchTimePeriod && matchTrace && matchApiEndpoint && matchGoogleId && matchRegkey
                    && matchEmail && matchLogEvent && matchSourceLocation && matchExceptionClass;
        };
    }

    /**
     * Allows mocking of {@link GeneralLogEntry}.
     */
    public static class MockGeneralLogEntry extends GeneralLogEntry {
        private final String apiEndpoint;
        private final UserInfo userInfo;
        private final String logEvent;
        private final String exceptionClass;

        public MockGeneralLogEntry(String logName, String severity, String trace, SourceLocation sourceLocation,
                long timestamp, String apiEndpoint, String googleId, String regkey, String email, String logEvent,
                String exceptionClass) {
            super(logName, severity, trace, sourceLocation, timestamp);
            this.apiEndpoint = apiEndpoint;
            this.userInfo = new UserInfo(googleId, regkey, email);
            this.logEvent = logEvent;
            this.exceptionClass = exceptionClass;
        }

        public String getApiEndpoint() {
            return apiEndpoint;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public String getLogEvent() {
            return logEvent;
        }

        public String getExceptionClass() {
            return exceptionClass;
        }

        /**
         * Represents user information.
         */
        public static class UserInfo {
            String googleId;
            String regkey;
            String email;

            public UserInfo(String google, String regkey, String email) {
                this.googleId = google;
                this.regkey = regkey;
                this.email = email;
            }

            public String getGoogleId() {
                return googleId;
            }

            public String getRegkey() {
                return regkey;
            }

            public String getEmail() {
                return email;
            }
        }
    }

}
