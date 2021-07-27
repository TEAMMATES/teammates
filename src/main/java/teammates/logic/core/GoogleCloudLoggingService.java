package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.gax.paging.Page;
import com.google.appengine.logging.v1.LogLine;
import com.google.appengine.logging.v1.RequestLog;
import com.google.appengine.logging.v1.SourceLocation;
import com.google.appengine.logging.v1.SourceReference;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.Logging.SortingField;
import com.google.cloud.logging.Logging.SortingOrder;
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
import teammates.common.datatransfer.QueryLogsParams;
import teammates.common.datatransfer.QueryLogsParams.UserInfoParams;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Holds functions for operations related to Google Cloud Logging.
 */
public class GoogleCloudLoggingService implements LogService {

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

    private static final String ASCENDING_ORDER = "asc";

    private static final String TRACE_PREFIX = String.format("projects/%s/traces/", Config.APP_ID);

    private final StudentsLogic studentsLogic = StudentsLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

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
    public QueryLogsResults queryLogs(QueryLogsParams queryLogsParams) throws LogServiceException {

        LogSearchParams logSearchParams = LogSearchParams.from(queryLogsParams)
                .addLogName(STDOUT_LOG_NAME)
                .addLogName(STDERR_LOG_NAME);

        PageParams pageParams = new PageParams(queryLogsParams.getPageSize(), queryLogsParams.getPageToken());

        Page<LogEntry> logEntriesInPage = getLogEntries(logSearchParams, pageParams);
        List<GeneralLogEntry> logEntries = new ArrayList<>();
        for (LogEntry entry : logEntriesInPage.getValues()) {
            String logName = entry.getLogName();
            Severity severity = entry.getSeverity();
            String trace = entry.getTrace();
            if (trace != null) {
                trace = trace.replace(TRACE_PREFIX, "");
            }
            com.google.cloud.logging.SourceLocation sourceLocation = entry.getSourceLocation();
            Map<String, String> resourceIdentifier = entry.getResource().getLabels();
            Payload<?> payload = entry.getPayload();
            long timestamp = entry.getTimestamp();

            GeneralLogEntry logEntry = new GeneralLogEntry(logName, severity.toString(), trace, resourceIdentifier,
                    new GeneralLogEntry.SourceLocation(sourceLocation.getFile(), sourceLocation.getLine(),
                            sourceLocation.getFunction()), timestamp);
            if (payload.getType() == Payload.Type.JSON) {
                Map<String, Object> jsonPayloadMap = ((Payload.JsonPayload) payload).getDataAsMap();
                logEntry.setDetails(jsonPayloadMap);
            } else {
                String textPayloadMessage = ((Payload.StringPayload) payload).getData();
                logEntry.setMessage(textPayloadMessage);
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
            String logNameFilter = s.logName.stream()
                    .map(str -> "\"projects/" + options.getProjectId() + "/logs/" + str + "\"")
                    .collect(Collectors.joining(" OR "));
            logFilters.add("logName=(" + logNameFilter + ")");
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
        if (s.severity != null) {
            logFilters.add("severity=" + s.severity);
        }
        if (s.minSeverity != null && s.severity == null) {
            logFilters.add("severity>=" + s.minSeverity.toString());
        }
        if (s.traceId != null) {
            logFilters.add("trace=\"" + s.traceId + "\"");
        }
        if (s.actionClass != null) {
            logFilters.add("jsonPayload.actionClass=\"" + s.actionClass + "\"");
        }
        if (s.userInfoParams != null) {
            if (s.userInfoParams.getGoogleId() != null) {
                logFilters.add("jsonPayload.userInfo.googleId=\"" + s.userInfoParams.getGoogleId() + "\"");
            }
            if (s.userInfoParams.getRegkey() != null) {
                logFilters.add("jsonPayload.userInfo.regkey=\"" + s.userInfoParams.getRegkey() + "\"");
            }
            if (s.userInfoParams.getEmail() != null) {
                logFilters.add("jsonPayload.userInfo.email=\"" + s.userInfoParams.getEmail() + "\"");
            }
        }
        if (s.logEvent != null) {
            logFilters.add("jsonPayload.event=\"" + s.logEvent + "\"");
        }
        if (s.sourceLocation != null && s.sourceLocation.getFile() != null) {
            if (s.sourceLocation.getFunction() == null) {
                logFilters.add("sourceLocation.file=\"" + s.sourceLocation.getFile() + "\"");
            } else {
                logFilters.add("sourceLocation.file=\"" + s.sourceLocation.getFile()
                        + "\" AND sourceLocation.function=\"" + s.sourceLocation.getFunction() + "\"");
            }
        }
        if (s.exceptionClass != null) {
            // TODO: investigate whether an exception happening equals to the exception name
            //  being passed to the textPayload.
            logFilters.add("textPayload:\"" + s.exceptionClass + "\"");
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

            if (s.order != null) {
                if (ASCENDING_ORDER.equals(s.order)) {
                    entryListOptions.add(EntryListOption.sortOrder(SortingField.TIMESTAMP, SortingOrder.ASCENDING));
                } else {
                    entryListOptions.add(EntryListOption.sortOrder(SortingField.TIMESTAMP, SortingOrder.DESCENDING));
                }
            }

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
        private String severity;
        private Map<String, String> labels = new HashMap<>();
        private Map<String, String> resourceLabels = new HashMap<>();
        private String traceId;
        private String actionClass;
        private UserInfoParams userInfoParams;
        private String logEvent;
        private GeneralLogEntry.SourceLocation sourceLocation;
        private String exceptionClass;
        private String order;

        public static LogSearchParams from(QueryLogsParams queryLogsParams) {
            LogSearchParams logSearchParams = new LogSearchParams()
                    .setStartTime(queryLogsParams.getStartTime())
                    .setEndTime(queryLogsParams.getEndTime())
                    .setActionClass(queryLogsParams.getActionClass())
                    .setUserInfoParams(queryLogsParams.getUserInfoParams())
                    .setLogEvent(queryLogsParams.getLogEvent())
                    .setSourceLocation(queryLogsParams.getSourceLocation())
                    .setExceptionClass(queryLogsParams.getExceptionClass())
                    .setOrder(queryLogsParams.getOrder());
            if (queryLogsParams.getSeverityLevel() != null) {
                logSearchParams.setSeverity(queryLogsParams.getSeverityLevel());
            } else if (queryLogsParams.getMinSeverity() != null) {
                logSearchParams.setMinSeverity(LogSeverity.valueOf(queryLogsParams.getMinSeverity()));
            } else {
                logSearchParams.setMinSeverity(LogSeverity.INFO);
            }
            if (queryLogsParams.getTraceId() != null) {
                logSearchParams.setTraceId(TRACE_PREFIX + queryLogsParams.getTraceId());
            }
            return logSearchParams;
        }

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

        public LogSearchParams setSeverity(String severity) {
            this.severity = severity;
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

        public LogSearchParams setTraceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public LogSearchParams setActionClass(String actionClass) {
            this.actionClass = actionClass;
            return this;
        }

        public LogSearchParams setUserInfoParams(UserInfoParams userInfoParams) {
            this.userInfoParams = userInfoParams;
            return this;
        }

        public LogSearchParams setLogEvent(String logEvent) {
            this.logEvent = logEvent;
            return this;
        }

        public LogSearchParams setSourceLocation(GeneralLogEntry.SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
            return this;
        }

        public LogSearchParams setExceptionClass(String exceptionClass) {
            this.exceptionClass = exceptionClass;
            return this;
        }

        public LogSearchParams setOrder(String order) {
            this.order = order;
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
