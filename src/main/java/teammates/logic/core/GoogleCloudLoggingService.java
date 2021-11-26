package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.gax.paging.Page;
import com.google.appengine.logging.v1.LogLine;
import com.google.appengine.logging.v1.RequestLog;
import com.google.appengine.logging.v1.SourceReference;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.Logging.SortingField;
import com.google.cloud.logging.Logging.SortingOrder;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.FeedbackSessionAuditLogDetails;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogDetails;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.SourceLocation;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;

/**
 * Holds functions for operations related to Google Cloud Logging.
 */
public class GoogleCloudLoggingService implements LogService {

    private static final String RESOURCE_TYPE_GAE_APP = "gae_app";

    private static final String REQUEST_LOG_NAME = "appengine.googleapis.com%2Frequest_log";
    private static final String STDOUT_LOG_NAME = "stdout";
    private static final String STDERR_LOG_NAME = "stderr";

    private static final String ASCENDING_ORDER = "asc";

    private static final String TRACE_PREFIX = String.format("projects/%s/traces/", Config.APP_ID);

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
                .setResourceType(RESOURCE_TYPE_GAE_APP);

        List<LogEntry> logEntries = new ArrayList<>();
        List<ErrorLogEntry> errorLogs = new ArrayList<>();

        Page<LogEntry> entries = getLogEntries(logSearchParams, 0);
        for (LogEntry entry : entries.iterateAll()) {
            logEntries.add(entry);
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
    public QueryLogsResults queryLogs(QueryLogsParams queryLogsParams) {

        LogSearchParams logSearchParams = LogSearchParams.from(queryLogsParams)
                .addLogName(STDOUT_LOG_NAME)
                .addLogName(STDERR_LOG_NAME)
                .setResourceType(RESOURCE_TYPE_GAE_APP);

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
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) {
        // This method is not necessary for production usage because a feedback session log
        // is already separately created through the standardized logging infrastructure.
        // However, this method is not removed as it is necessary to assist in local testing.
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            long startTime, long endTime, String fsName) {
        List<String> filters = new ArrayList<>();
        if (courseId != null) {
            filters.add("jsonPayload.courseId=\"" + courseId + "\"");
        }
        if (email != null) {
            filters.add("jsonPayload.studentEmail=\"" + email + "\"");
        }
        if (fsName != null) {
            filters.add("jsonPayload.feedbackSessionName=\"" + fsName + "\"");
        }
        QueryLogsParams queryLogsParams = QueryLogsParams.builder(startTime, endTime)
                .withLogEvent(LogEvent.FEEDBACK_SESSION_AUDIT.name())
                .withSeverityLevel(LogSeverity.INFO)
                .withExtraFilters(String.join("\n", filters))
                .build();
        LogSearchParams logSearchParams = LogSearchParams.from(queryLogsParams)
                .addLogName(STDOUT_LOG_NAME)
                .setResourceType(RESOURCE_TYPE_GAE_APP);
        Page<LogEntry> entries = getLogEntries(logSearchParams, 0);
        List<LogEntry> logEntries = new ArrayList<>();
        for (LogEntry entry : entries.iterateAll()) {
            logEntries.add(entry);
        }

        List<FeedbackSessionLogEntry> fsLogEntries = new ArrayList<>();
        for (LogEntry entry : logEntries) {
            long timestamp = entry.getTimestamp();
            Payload<?> payload = entry.getPayload();
            FeedbackSessionAuditLogDetails details;
            if (payload.getType() == Payload.Type.JSON) {
                Map<String, Object> jsonPayloadMap = ((Payload.JsonPayload) payload).getDataAsMap();
                LogDetails logDetails = JsonUtils.fromJson(JsonUtils.toCompactJson(jsonPayloadMap), LogDetails.class);
                if (!(logDetails instanceof FeedbackSessionAuditLogDetails)) {
                    continue;
                }
                details = (FeedbackSessionAuditLogDetails) logDetails;
            } else {
                continue;
            }

            FeedbackSessionLogEntry fslEntry = new FeedbackSessionLogEntry(details.getStudentEmail(),
                    details.getFeedbackSessionName(), details.getAccessType(), timestamp);
            fsLogEntries.add(fslEntry);
        }

        return fsLogEntries;
    }

    private Page<LogEntry> getLogEntries(LogSearchParams s, int pageSize) {
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
        String logFilter = logFilters.stream().collect(Collectors.joining("\n"));

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

        Logging logging = options.getService();
        Page<LogEntry> entries = logging.listLogEntries(entryListOptions.toArray(new EntryListOption[] {}));

        try {
            logging.close();
        } catch (Exception e) {
            // ignore exception when closing resource
        }
        return entries;
    }

    /**
     * Contains params to be used for the searching of logs.
     */
    private static class LogSearchParams {
        private List<String> logName = new ArrayList<>();
        private String resourceType;
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
    }

}
