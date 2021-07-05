package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.google.api.gax.paging.Page;
import com.google.appengine.logging.v1.LogLine;
import com.google.appengine.logging.v1.RequestLog;
import com.google.appengine.logging.v1.SourceLocation;
import com.google.appengine.logging.v1.SourceReference;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.cloud.logging.Severity;
import com.google.logging.type.LogSeverity;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.GeneralLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Const;

/**
 * Holds functions for operations related to Google Cloud Logging.
 */
public class GoogleCloudLoggingService implements LogService {
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    private static final String REQUEST_LOG_NAME = "appengine.googleapis.com%2Frequest_log";
    private static final String REQUEST_LOG_RESOURCE_TYPE = "gae_app";
    private static final String REQUEST_LOG_MODULE_ID_LABEL = "module_id";
    private static final String REQUEST_LOG_MODULE_ID_LABEL_VALUE = "default";

    private static final String FEEDBACK_SESSION_LOG_NAME = "feedback-session-logs";
    private static final String FEEDBACK_SESSION_LOG_COURSE_ID_LABEL = "courseId";
    private static final String FEEDBACK_SESSION_LOG_EMAIL_LABEL = "email";
    private static final String FEEDBACK_SESSION_LOG_NAME_LABEL = "fsName";
    private static final String FEEDBACK_SESSION_LOG_TYPE_LABEL = "fslType";

    private static final String STDOUT_LOG_NAME = "stdout";
    private static final String STDERR_LOG_NAME = "stderr";

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        Instant endTime = Instant.now();
        // Sets the range to 6 minutes to slightly overlap the 5 minute email timer
        long queryRange = 1000 * 60 * 6;
        Instant startTime = endTime.minusMillis(queryRange);

        LogSearchParams logSearchParams = new LogSearchParams()
                .addLogName(REQUEST_LOG_NAME)
                .setResourceType(REQUEST_LOG_RESOURCE_TYPE)
                .addResourceLabel(REQUEST_LOG_MODULE_ID_LABEL, REQUEST_LOG_MODULE_ID_LABEL_VALUE)
                .setMinSeverity(LogSeverity.ERROR)
                .setStartTime(startTime)
                .setEndTime(endTime);

        List<LogEntry> logEntries = new ArrayList<>();
        List<ErrorLogEntry> errorLogs = new ArrayList<>();

        try {
            Page<LogEntry> entries = getLogEntries(logSearchParams, null);
            for (LogEntry entry : entries.iterateAll()) {
                logEntries.add(entry);
            }
        } catch (LogServiceException e) {
            // TODO
        }

        for (LogEntry logEntry : logEntries) {
            Any entry = (Any) logEntry.getPayload().getData();

            JsonFormat.TypeRegistry tr = JsonFormat.TypeRegistry.newBuilder()
                    .add(RequestLog.getDescriptor())
                    .add(LogLine.getDescriptor())
                    .add(SourceLocation.getDescriptor())
                    .add(SourceReference.getDescriptor())
                    .build();

            List<LogLine> logLines = new ArrayList<>();
            try {
                String logContentAsJson = JsonFormat.printer().usingTypeRegistry(tr).print(entry);

                RequestLog.Builder builder = RequestLog.newBuilder();
                JsonFormat.parser().ignoringUnknownFields().usingTypeRegistry(tr).merge(logContentAsJson, builder);
                RequestLog reconvertedLog = builder.build();

                logLines = reconvertedLog.getLineList();
            } catch (InvalidProtocolBufferException e) {
                // TODO
            }

            for (LogLine line : logLines) {
                if (line.getSeverity() == LogSeverity.ERROR || line.getSeverity() == LogSeverity.CRITICAL) {
                    errorLogs.add(new ErrorLogEntry(
                            line.getLogMessage().replaceAll("\n", "\n<br>"),
                            line.getSeverity().toString())
                    );
                }
            }
        }
        return errorLogs;
    }

    @Override
    public QueryLogsResults queryLogs(List<String> severities, Instant startTime, Instant endTime,
            Integer pageSize, String pageToken) throws LogServiceException {
        LogSearchParams logSearchParams = new LogSearchParams()
                .addLogName(STDOUT_LOG_NAME)
                .addLogName(STDERR_LOG_NAME)
                .setSeverities(severities)
                .setStartTime(startTime)
                .setEndTime(endTime);

        PageParams pageParams = new PageParams(pageSize, pageToken);

        Page<LogEntry> logEntriesInPage = getLogEntries(logSearchParams, pageParams);
        List<GeneralLogEntry> logEntries = new ArrayList<>();
        for (LogEntry entry : logEntriesInPage.getValues()) {
            String logName = entry.getLogName();
            Severity severity = entry.getSeverity();
            String trace = entry.getTrace();
            com.google.cloud.logging.SourceLocation sourceLocation = entry.getSourceLocation();
            Payload<?> payload = entry.getPayload();
            long timestamp = entry.getTimestamp();

            GeneralLogEntry logEntry;
            if (payload.getType() == Payload.Type.JSON) {
                JSONObject jsonObject = new JSONObject(((Payload.JsonPayload) payload).getDataAsMap());
                logEntry = new GeneralLogEntry(logName, severity.toString(), trace,
                        new GeneralLogEntry.SourceLocation(sourceLocation.getFile(), sourceLocation.getLine(),
                                sourceLocation.getFunction()), payload, timestamp, jsonObject);
            } else {
                logEntry = new GeneralLogEntry(logName, severity.toString(), trace,
                        new GeneralLogEntry.SourceLocation(sourceLocation.getFile(), sourceLocation.getLine(),
                                sourceLocation.getFunction()), payload, timestamp);
            }
            logEntries.add(logEntry);
        }
        String nextPageToken = logEntriesInPage.getNextPageToken();
        return new QueryLogsResults(logEntries, nextPageToken);
    }

    @Override
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType)
            throws LogServiceException {
        String payload = "Feedback session log: course ID=" + courseId + ", email=" + email
                + ", feedback session name=" + fsName + ", log type=" + fslType;
        LogEntry entry = LogEntry.newBuilder(StringPayload.of(payload))
                .setLogName(FEEDBACK_SESSION_LOG_NAME)
                .addLabel(FEEDBACK_SESSION_LOG_COURSE_ID_LABEL, courseId)
                .addLabel(FEEDBACK_SESSION_LOG_EMAIL_LABEL, email)
                .addLabel(FEEDBACK_SESSION_LOG_NAME_LABEL, fsName)
                .addLabel(FEEDBACK_SESSION_LOG_TYPE_LABEL, fslType)
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();
        createLogEntry(entry);
    }

    private void createLogEntry(LogEntry entry) throws LogServiceException {
        try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {
            logging.write(Collections.singleton(entry));
        } catch (Exception e) {
            throw new LogServiceException(e);
        }
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName) throws LogServiceException {
        LogSearchParams logSearchParams = new LogSearchParams()
                .addLogName(FEEDBACK_SESSION_LOG_NAME)
                .addLabel(FEEDBACK_SESSION_LOG_COURSE_ID_LABEL, courseId)
                .addLabel(FEEDBACK_SESSION_LOG_EMAIL_LABEL, email)
                .addLabel(FEEDBACK_SESSION_LOG_NAME_LABEL, fsName)
                .setStartTime(startTime)
                .setEndTime(endTime);
        Page<LogEntry> entries = getLogEntries(logSearchParams, null);
        List<LogEntry> logEntries = new ArrayList<>();
        for (LogEntry entry : entries.iterateAll()) {
            logEntries.add(entry);
        }

        List<FeedbackSessionLogEntry> fsLogEntries = new ArrayList<>();
        for (LogEntry entry : logEntries) {
            String fslType = entry.getLabels().get(FEEDBACK_SESSION_LOG_TYPE_LABEL);
            long timestamp = entry.getTimestamp();
            String entryEmail = entry.getLabels().get(FEEDBACK_SESSION_LOG_EMAIL_LABEL);
            String entryFsName = entry.getLabels().get(FEEDBACK_SESSION_LOG_NAME_LABEL);
            StudentAttributes student = studentsLogic.getStudentForEmail(courseId, entryEmail);
            FeedbackSessionAttributes fs = fsLogic.getFeedbackSession(entryFsName, courseId);
            if (student == null || fs == null) {
                // If the student email or feedback session retrieved from the logs are invalid, discard it
                continue;
            }
            if (!fslType.equals(Const.FeedbackSessionLogTypes.ACCESS)
                    && !fslType.equals(Const.FeedbackSessionLogTypes.SUBMISSION)
                    && !fslType.equals(Const.FeedbackSessionLogTypes.VIEW_RESULT)) {
                // If the feedback session log type retrieved from the logs is invalid, discard it
                continue;
            }

            FeedbackSessionLogEntry fslEntry = new FeedbackSessionLogEntry(student, fs, fslType, timestamp);
            fsLogEntries.add(fslEntry);
        }

        return fsLogEntries;
    }

    private Page<LogEntry> getLogEntries(LogSearchParams s, PageParams p) throws LogServiceException {
        LoggingOptions options = LoggingOptions.getDefaultInstance();

        List<String> logFilters = new ArrayList<>();
        if (!s.logName.isEmpty()) {
            String logNameFilter = s.logName.stream().reduce((subtotal, element) ->
                    subtotal + "\" OR \"projects/" + options.getProjectId() + "/logs/" + element).get();
            logFilters.add("logName=(\"projects/" + options.getProjectId() + "/logs/" + logNameFilter + "\")");
        }
        if (s.resourceType != null) {
            logFilters.add("resource.type=\"" + s.resourceType + "\"");
        }
        if (s.startTime != null) {
            logFilters.add("timestamp>\"" + s.startTime.toString() + "\"");
        }
        if (s.endTime != null) {
            logFilters.add("timestamp<=\"" + s.endTime.toString() + "\"");
        }
        if (s.severities != null) {
            String severitiesFilter = s.severities.stream().reduce((subtotal, element) ->
                    subtotal + " OR " + element).get();
            logFilters.add("severity=(" + severitiesFilter + ")");
        }
        if (s.minSeverity != null && s.severities == null) {
            logFilters.add("severity>=" + s.minSeverity.toString());
        }
        for (Map.Entry<String, String> entry : s.labels.entrySet()) {
            logFilters.add("labels." + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        for (Map.Entry<String, String> entry : s.resourceLabels.entrySet()) {
            logFilters.add("resource.labels." + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        String logFilter = logFilters.stream().collect(Collectors.joining("\n"));

        Page<LogEntry> entries;

        try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {
            List<EntryListOption> entryListOptions = new ArrayList<>();

            entryListOptions.add(EntryListOption.filter(logFilter));

            if (p != null) {
                if (p.pageSize != null) {
                    entryListOptions.add(EntryListOption.pageSize(p.pageSize));
                }
                if (p.nextPageToken != null) {
                    entryListOptions.add(EntryListOption.pageToken(p.nextPageToken));
                }
            }

            EntryListOption[] entryListOptionsArray = new EntryListOption[entryListOptions.size()];
            for (int i = 0; i < entryListOptions.size(); i++) {
                entryListOptionsArray[i] = entryListOptions.get(i);
            }

            entries = logging.listLogEntries(entryListOptionsArray);
        } catch (Exception e) {
            throw new LogServiceException(e);
        }
        return entries;
    }

    /**
     * Contains params to be used for the searching of logs.
     */
    private static class LogSearchParams {
        private List<String> logName = new ArrayList<>();
        private String resourceType;
        private Instant startTime;
        private Instant endTime;
        private LogSeverity minSeverity;
        private List<String> severities;
        private Map<String, String> labels = new HashMap<>();
        private Map<String, String> resourceLabels = new HashMap<>();

        public LogSearchParams addLogName(String logName) {
            this.logName.add(logName);
            return this;
        }

        public LogSearchParams setResourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public LogSearchParams setStartTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public LogSearchParams setEndTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public LogSearchParams setMinSeverity(LogSeverity minSeverity) {
            this.minSeverity = minSeverity;
            return this;
        }

        public LogSearchParams setSeverities(List<String> severities) {
            this.severities = severities;
            return this;
        }

        public LogSearchParams addLabel(String key, String value) {
            if (key != null && value != null) {
                this.labels.put(key, value);
            }
            return this;
        }

        public LogSearchParams addResourceLabel(String key, String value) {
            if (key != null && value != null) {
                this.resourceLabels.put(key, value);
            }
            return this;
        }
    }

    /**
     * Contains params for pagination.
     */
    private static class PageParams {
        private Integer pageSize;
        private String nextPageToken;

        PageParams(int pageSize) {
            this.pageSize = pageSize;
        }

        PageParams(String nextPageToken) {
            this.nextPageToken = nextPageToken;
        }

        PageParams(int pageSize, String nextPageToken) {
            this.pageSize = pageSize;
            this.nextPageToken = nextPageToken;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public String getNextPageToken() {
            return nextPageToken;
        }
    }

}
