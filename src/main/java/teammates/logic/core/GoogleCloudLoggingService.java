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
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogDetails;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.SourceLocation;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;

/**
 * Holds functions for operations related to Google Cloud Logging.
 */
public class GoogleCloudLoggingService implements LogService {

    private static final Logger log = Logger.getLogger();

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

        QueryLogsParams queryLogsParams = QueryLogsParams.builder(startTime.toEpochMilli(), endTime.toEpochMilli())
                .withMinSeverity(LogSeverity.ERROR)
                .build();
        LogSearchParams logSearchParams = LogSearchParams.from(queryLogsParams)
                .addLogName(REQUEST_LOG_NAME)
                .setResourceType(REQUEST_LOG_RESOURCE_TYPE)
                .addResourceLabel(REQUEST_LOG_MODULE_ID_LABEL, REQUEST_LOG_MODULE_ID_LABEL_VALUE);

        List<LogEntry> logEntries = new ArrayList<>();
        List<ErrorLogEntry> errorLogs = new ArrayList<>();

        try {
            Page<LogEntry> entries = getLogEntries(logSearchParams, 0);
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
                    .add(com.google.appengine.logging.v1.SourceLocation.getDescriptor())
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

            String trace = logEntry.getTrace();
            if (trace != null) {
                trace = trace.replace(TRACE_PREFIX, "");
            }

            for (LogLine line : logLines) {
                if (line.getSeverity().getNumber() >= com.google.logging.type.LogSeverity.ERROR.getNumber()) {
                    errorLogs.add(new ErrorLogEntry(
                            line.getLogMessage().replaceAll("\n", "\n<br>"),
                            line.getSeverity().toString(), trace)
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

        Page<LogEntry> logEntriesInPage = getLogEntries(logSearchParams, queryLogsParams.getPageSize());
        List<GeneralLogEntry> logEntries = new ArrayList<>();
        for (LogEntry entry : logEntriesInPage.getValues()) {
            Severity severity = entry.getSeverity();
            String trace = entry.getTrace();
            if (trace != null) {
                trace = trace.replace(TRACE_PREFIX, "");
            }
            String insertId = entry.getInsertId();
            com.google.cloud.logging.SourceLocation sourceLocation = entry.getSourceLocation();
            Map<String, String> resourceIdentifier = entry.getResource().getLabels();
            Payload<?> payload = entry.getPayload();
            long timestamp = entry.getTimestamp();

            String file = "";
            Long line = 0L;
            String function = "";
            if (sourceLocation != null) {
                file = sourceLocation.getFile();
                line = sourceLocation.getLine();
                function = sourceLocation.getFunction();
            }

            GeneralLogEntry logEntry = new GeneralLogEntry(convertSeverity(severity), trace, insertId,
                    resourceIdentifier, new SourceLocation(file, line, function), timestamp);
            if (payload.getType() == Payload.Type.JSON) {
                Map<String, Object> jsonPayloadMap = ((Payload.JsonPayload) payload).getDataAsMap();
                logEntry.setDetails(JsonUtils.fromJson(JsonUtils.toCompactJson(jsonPayloadMap), LogDetails.class));
            } else {
                String textPayloadMessage = ((Payload.StringPayload) payload).getData();
                logEntry.setMessage(textPayloadMessage);
            }
            logEntries.add(logEntry);
        }
        boolean hasNextPage = logEntriesInPage.getNextPageToken() != null;
        return new QueryLogsResults(logEntries, hasNextPage);
    }

    private LogSeverity convertSeverity(Severity severity) {
        if (severity == Severity.ERROR) {
            return LogSeverity.ERROR;
        }
        if (severity == Severity.WARNING) {
            return LogSeverity.WARNING;
        }
        if (severity == Severity.INFO || severity == Severity.NOTICE) {
            return LogSeverity.INFO;
        }
        if (severity == Severity.CRITICAL || severity == Severity.ALERT || severity == Severity.EMERGENCY) {
            return LogSeverity.CRITICAL;
        }
        if (severity == Severity.DEBUG) {
            return LogSeverity.DEBUG;
        }
        return LogSeverity.DEFAULT;
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
            log.severe("Error while creating log entry", e);
            throw new LogServiceException(e);
        }
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            long startTime, long endTime, String fsName) throws LogServiceException {
        QueryLogsParams queryLogsParams = QueryLogsParams.builder(startTime, endTime)
                .build();
        LogSearchParams logSearchParams = LogSearchParams.from(queryLogsParams)
                .addLogName(FEEDBACK_SESSION_LOG_NAME)
                .addLabel(FEEDBACK_SESSION_LOG_COURSE_ID_LABEL, courseId)
                .addLabel(FEEDBACK_SESSION_LOG_EMAIL_LABEL, email)
                .addLabel(FEEDBACK_SESSION_LOG_NAME_LABEL, fsName);
        Page<LogEntry> entries = getLogEntries(logSearchParams, 0);
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
            FeedbackSessionLogType convertedFslType = FeedbackSessionLogType.valueOfLabel(fslType);
            if (convertedFslType == null) {
                // If the feedback session log type retrieved from the logs is invalid, discard it
                continue;
            }

            FeedbackSessionLogEntry fslEntry = new FeedbackSessionLogEntry(student, fs, fslType, timestamp);
            fsLogEntries.add(fslEntry);
        }

        return fsLogEntries;
    }

    private Page<LogEntry> getLogEntries(LogSearchParams s, int pageSize) throws LogServiceException {
        LoggingOptions options = LoggingOptions.getDefaultInstance();
        QueryLogsParams q = s.queryLogsParams;

        List<String> logFilters = new ArrayList<>();
        logFilters.add("timestamp>\"" + Instant.ofEpochMilli(q.getStartTime()).toString() + "\"");
        logFilters.add("timestamp<=\"" + Instant.ofEpochMilli(q.getEndTime()).toString() + "\"");

        if (!s.logName.isEmpty()) {
            String logNameFilter = s.logName.stream()
                    .map(str -> "\"projects/" + options.getProjectId() + "/logs/" + str + "\"")
                    .collect(Collectors.joining(" OR "));
            logFilters.add("logName=(" + logNameFilter + ")");
        }
        if (s.resourceType != null) {
            logFilters.add("resource.type=\"" + s.resourceType + "\"");
        }
        if (q.getSeverity() != null) {
            logFilters.add("severity=" + q.getSeverity());
        } else if (q.getMinSeverity() != null && q.getSeverity() == null) {
            logFilters.add("severity>=" + q.getMinSeverity());
        }
        if (q.getTraceId() != null) {
            logFilters.add("trace=\"" + TRACE_PREFIX + q.getTraceId() + "\"");
        }
        if (q.getActionClass() != null) {
            logFilters.add("jsonPayload.actionClass=\"" + q.getActionClass() + "\"");
        }
        if (q.getUserInfoParams() != null) {
            if (q.getUserInfoParams().getGoogleId() != null) {
                logFilters.add("jsonPayload.userInfo.googleId=\"" + q.getUserInfoParams().getGoogleId() + "\"");
            }
            if (q.getUserInfoParams().getRegkey() != null) {
                logFilters.add("jsonPayload.userInfo.regkey=\"" + q.getUserInfoParams().getRegkey() + "\"");
            }
            if (q.getUserInfoParams().getEmail() != null) {
                logFilters.add("jsonPayload.userInfo.email=\"" + q.getUserInfoParams().getEmail() + "\"");
            }
        }
        if (q.getLogEvent() != null) {
            logFilters.add("jsonPayload.event=\"" + q.getLogEvent() + "\"");
        }
        if (q.getSourceLocation() != null && q.getSourceLocation().getFile() != null) {
            if (q.getSourceLocation().getFunction() == null) {
                logFilters.add("sourceLocation.file=\"" + q.getSourceLocation().getFile() + "\"");
            } else {
                logFilters.add("sourceLocation.file=\"" + q.getSourceLocation().getFile()
                        + "\" AND sourceLocation.function=\"" + q.getSourceLocation().getFunction() + "\"");
            }
        }
        if (q.getExceptionClass() != null) {
            logFilters.add("jsonPayload.exceptionClass=\"" + q.getExceptionClass() + "\"");
        }
        if (q.getLatency() != null) {
            logFilters.add("jsonPayload.responseTime" + q.getLatency());
        }
        if (q.getStatus() != null) {
            logFilters.add("jsonPayload.responseStatus=" + q.getStatus());
        }
        if (q.getVersion() != null) {
            logFilters.add("resource.labels.version_id=\"" + q.getVersion() + "\"");
        }
        if (q.getExtraFilters() != null) {
            logFilters.add(q.getExtraFilters());
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

            if (pageSize > 0) {
                entryListOptions.add(EntryListOption.pageSize(pageSize));
            }

            if (q.getOrder() != null) {
                if (ASCENDING_ORDER.equals(q.getOrder())) {
                    entryListOptions.add(EntryListOption.sortOrder(SortingField.TIMESTAMP, SortingOrder.ASCENDING));
                } else {
                    entryListOptions.add(EntryListOption.sortOrder(SortingField.TIMESTAMP, SortingOrder.DESCENDING));
                }
            }

            EntryListOption[] entryListOptionsArray = new EntryListOption[entryListOptions.size()];
            for (int i = 0; i < entryListOptions.size(); i++) {
                entryListOptionsArray[i] = entryListOptions.get(i);
            }

            entries = logging.listLogEntries(entryListOptionsArray);
        } catch (Exception e) {
            log.severe("Error while fetching logs", e);
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
        private Map<String, String> labels = new HashMap<>();
        private Map<String, String> resourceLabels = new HashMap<>();
        private QueryLogsParams queryLogsParams;

        private static LogSearchParams from(QueryLogsParams queryLogsParams) {
            return new LogSearchParams().setQueryLogsParams(queryLogsParams);
        }

        private LogSearchParams addLogName(String logName) {
            this.logName.add(logName);
            return this;
        }

        private LogSearchParams setResourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        private LogSearchParams setQueryLogsParams(QueryLogsParams queryLogsParams) {
            this.queryLogsParams = queryLogsParams;
            return this;
        }

        private LogSearchParams addLabel(String key, String value) {
            if (key != null && value != null) {
                this.labels.put(key, value);
            }
            return this;
        }

        private LogSearchParams addResourceLabel(String key, String value) {
            if (key != null && value != null) {
                this.resourceLabels.put(key, value);
            }
            return this;
        }
    }

}
