package teammates.logic.external;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.math3.random.RandomDataGenerator;

import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.ExceptionLogDetails;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogDetails;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.RequestLogDetails;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.util.FileHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;

/**
 * Holds functions for operations related to logs reading/writing in local dev environment.
 *
 * <p>The current implementation uses an in-memory storage of logs for local query testing.
 */
public class LocalLoggingService implements LogService {

    private static final List<GeneralLogEntry> LOCAL_LOG_ENTRIES = loadLocalLogEntries();
    private static final String ASCENDING_ORDER = "asc";

    private static List<GeneralLogEntry> loadLocalLogEntries() {
        // Timestamp of logs are randomly created to be within the last one hour
        long currentTimestamp = Instant.now().toEpochMilli();
        long earliestTimestamp = currentTimestamp - 60 * 60 * 1000;
        try {
            String jsonString = FileHelper.readResourceFile("logsForLocalDev.json");
            Collection<GeneralLogEntry> logEntriesCollection = JsonUtils.fromJson(jsonString, new TypeReference<>(){});
            return logEntriesCollection.stream()
                    .map(log -> {
                        long timestamp = new RandomDataGenerator().nextLong(earliestTimestamp, currentTimestamp);
                        GeneralLogEntry logEntryWithUpdatedTimestamp = new GeneralLogEntry(
                                log.getSeverity(), log.getTrace(), log.getInsertId(), log.getResourceIdentifier(),
                                log.getSourceLocation(), timestamp);
                        logEntryWithUpdatedTimestamp.setDetails(log.getDetails());
                        logEntryWithUpdatedTimestamp.setMessage(log.getMessage());
                        return logEntryWithUpdatedTimestamp;
                    })
                    .collect(Collectors.toList());
        } catch (JacksonException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public QueryLogsResults queryLogs(QueryLogsParams queryLogsParams) {
        // Page size is set as a small value to test loading of more logs
        int pageSize = 10;

        List<GeneralLogEntry> result = LOCAL_LOG_ENTRIES.stream()
                .sorted((x, y) -> {
                    String order = queryLogsParams.getOrder();
                    if (ASCENDING_ORDER.equals(order)) {
                        return Long.compare(x.getTimestamp(), y.getTimestamp());
                    } else {
                        return Long.compare(y.getTimestamp(), x.getTimestamp());
                    }
                })
                .filter(log -> queryLogsParams.getSeverity() == null
                        || log.getSeverity() == queryLogsParams.getSeverity())
                .filter(log -> queryLogsParams.getMinSeverity() == null
                        || log.getSeverity().getSeverityLevel()
                            >= queryLogsParams.getMinSeverity().getSeverityLevel())
                .filter(log -> log.getTimestamp() > queryLogsParams.getStartTime())
                .filter(log -> log.getTimestamp() <= queryLogsParams.getEndTime())
                .filter(log -> queryLogsParams.getTraceId() == null
                        || queryLogsParams.getTraceId().equals(log.getTrace()))
                .filter(log -> queryLogsParams.getVersion() == null
                        || queryLogsParams.getVersion().equals(log.getResourceIdentifier().get("version_id")))
                .filter(log -> queryLogsParams.getSourceLocation().getFile() == null
                        || log.getSourceLocation().getFile().equals(queryLogsParams.getSourceLocation().getFile()))
                .filter(log -> queryLogsParams.getSourceLocation().getFunction() == null
                        || log.getSourceLocation().getFunction().equals(queryLogsParams.getSourceLocation().getFunction()))
                .filter(log -> isEventBasedFilterSatisfied(log, queryLogsParams))
                .limit(pageSize)
                .collect(Collectors.toList());

        List<GeneralLogEntry> copiedResults = deepCopyLogEntries(result);
        boolean hasNextPage = copiedResults.size() == pageSize;

        return new QueryLogsResults(copiedResults, hasNextPage);
    }

    private boolean isEventBasedFilterSatisfied(GeneralLogEntry log, QueryLogsParams queryLogsParams) {
        String actionClassFilter = queryLogsParams.getActionClass();
        String exceptionClassFilter = queryLogsParams.getExceptionClass();
        String logEventFilter = queryLogsParams.getLogEvent();
        String latencyFilter = queryLogsParams.getLatency();
        String statusFilter = queryLogsParams.getStatus();

        RequestLogUser userInfoFilter = queryLogsParams.getUserInfoParams();
        String emailFilter = userInfoFilter.getEmail();
        String googleIdFilter = userInfoFilter.getGoogleId();

        if (actionClassFilter == null && exceptionClassFilter == null && logEventFilter == null
                && latencyFilter == null && statusFilter == null
                && emailFilter == null && googleIdFilter == null) {
            return true;
        }
        LogDetails details = log.getDetails();
        if (details == null) {
            return false;
        }
        if (logEventFilter != null && !details.getEvent().name().equals(logEventFilter)) {
            return false;
        }
        if (!isExceptionFilterSatisfied(details, exceptionClassFilter)) {
            return false;
        }
        return isRequestFilterSatisfied(details, actionClassFilter, latencyFilter, statusFilter,
                emailFilter, googleIdFilter);
    }

    private boolean isExceptionFilterSatisfied(LogDetails details, String exceptionClassFilter) {
        if (exceptionClassFilter == null) {
            return true;
        }
        if (details.getEvent() != LogEvent.EXCEPTION_LOG) {
            return false;
        }
        ExceptionLogDetails exceptionDetails = (ExceptionLogDetails) details;
        return exceptionDetails.getExceptionClass().equals(exceptionClassFilter);
    }

    private boolean isRequestFilterSatisfied(LogDetails details, String actionClassFilter,
            String latencyFilter, String statusFilter, String emailFilter, String googleIdFilter) {
        if (actionClassFilter == null && latencyFilter == null && statusFilter == null
                 && emailFilter == null && googleIdFilter == null) {
            return true;
        }
        if (details.getEvent() != LogEvent.REQUEST_LOG) {
            return false;
        }
        RequestLogDetails requestDetails = (RequestLogDetails) details;
        if (actionClassFilter != null && !actionClassFilter.equals(requestDetails.getActionClass())) {
            return false;
        }
        if (statusFilter != null && !statusFilter.equals(String.valueOf(requestDetails.getResponseStatus()))) {
            return false;
        }
        if (latencyFilter != null) {
            Pattern p = Pattern.compile("^(>|>=|<|<=) *(\\d+)$");
            Matcher m = p.matcher(latencyFilter);
            long logLatency = ((RequestLogDetails) details).getResponseTime();
            boolean isFilterSatisfied = false;
            if (m.matches()) {
                int time = Integer.parseInt(m.group(2));
                switch (m.group(1)) {
                case ">":
                    isFilterSatisfied = logLatency > time;
                    break;
                case ">=":
                    isFilterSatisfied = logLatency >= time;
                    break;
                case "<":
                    isFilterSatisfied = logLatency < time;
                    break;
                case "<=":
                    isFilterSatisfied = logLatency <= time;
                    break;
                default:
                    assert false : "Unreachable case";
                    break;
                }
            }
            if (!isFilterSatisfied) {
                return false;
            }
        }
        RequestLogUser userInfo = requestDetails.getUserInfo();
        if (emailFilter != null && (userInfo == null
                || !SanitizationHelper.areEmailsEqual(emailFilter, userInfo.getEmail()))) {
            return false;
        }
        return googleIdFilter == null || userInfo != null && googleIdFilter.equals(userInfo.getGoogleId());
    }

    private List<GeneralLogEntry> deepCopyLogEntries(List<GeneralLogEntry> logEntries) {
        List<GeneralLogEntry> result = new ArrayList<>();
        for (GeneralLogEntry logEntry : logEntries) {
            GeneralLogEntry copiedEntry = new GeneralLogEntry(logEntry.getSeverity(),
                    logEntry.getTrace(), logEntry.getInsertId(), logEntry.getResourceIdentifier(),
                    logEntry.getSourceLocation(), logEntry.getTimestamp());
            copiedEntry.setDetails(JsonUtils.fromJson(JsonUtils.toCompactJson(logEntry.getDetails()), LogDetails.class));
            copiedEntry.setMessage(logEntry.getMessage());
            result.add(copiedEntry);
        }

        return result;
    }
}
