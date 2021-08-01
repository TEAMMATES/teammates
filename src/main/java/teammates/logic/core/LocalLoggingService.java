package teammates.logic.core;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonParseException;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.logs.GeneralLogEntry;
import teammates.common.datatransfer.logs.LogSeverity;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.datatransfer.logs.RequestLogUser;
import teammates.common.util.FileHelper;
import teammates.common.util.JsonUtils;

/**
 * Holds functions for operations related to logs reading/writing in local dev environment.
 *
 * <p>The current implementation uses an in-memory storage of logs to simulate the logs
 * retention locally for feedback session logs only. It is not meant as a replacement but
 * merely for testing purposes.
 */
public class LocalLoggingService implements LogService {

    private static final List<FeedbackSessionLogEntry> FEEDBACK_SESSION_LOG_ENTRIES = new ArrayList<>();
    private static final List<GeneralLogEntry> LOCAL_LOG_ENTRIES = loadLocalLogEntries();
    private static final String ASCENDING_ORDER = "asc";

    private final StudentsLogic studentsLogic = StudentsLogic.inst();
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    private static List<GeneralLogEntry> loadLocalLogEntries() {
        // Timestamp of logs are randomly created to be within the last one hour
        long currentTimestamp = Instant.now().toEpochMilli();
        long earliestTimestamp = currentTimestamp - 60 * 60 * 1000;
        try {
            String jsonString = FileHelper.readResourceFile("logsForLocalDev.json");
            Type type = new TypeToken<Collection<GeneralLogEntry>>(){}.getType();
            Collection<GeneralLogEntry> logEntriesCollection = JsonUtils.fromJson(jsonString, type);
            return logEntriesCollection.stream()
                    .map(log -> {
                        long timestamp = new RandomDataGenerator().nextLong(earliestTimestamp, currentTimestamp);
                        GeneralLogEntry logEntryWithUpdatedTimestamp = new GeneralLogEntry(log.getLogName(),
                                log.getSeverity(), log.getTrace(), log.getInsertId(), log.getResourceIdentifier(),
                                log.getSourceLocation(), timestamp);
                        logEntryWithUpdatedTimestamp.setDetails(log.getDetails());
                        logEntryWithUpdatedTimestamp.setMessage(log.getMessage());
                        return logEntryWithUpdatedTimestamp;
                    })
                    .collect(Collectors.toList());
        } catch (JsonParseException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        // Not supported in dev server
        return new ArrayList<>();
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
                .filter(logs -> queryLogsParams.getSeverity() == null
                        || logs.getSeverity().equals(queryLogsParams.getSeverity()))
                .filter(logs -> queryLogsParams.getMinSeverity() == null
                        || LogSeverity.valueOf(logs.getSeverity()).getSeverityLevel()
                            >= LogSeverity.valueOf(queryLogsParams.getMinSeverity()).getSeverityLevel())
                .filter(logs -> logs.getTimestamp() > queryLogsParams.getStartTime())
                .filter(logs -> logs.getTimestamp() <= queryLogsParams.getEndTime())
                .filter(logs -> queryLogsParams.getTraceId() == null
                        || (logs.getTrace() != null && logs.getTrace().equals(queryLogsParams.getTraceId())))
                .filter(logs -> queryLogsParams.getActionClass() == null
                        || (logs.getDetails() != null && logs.getDetails().get("actionClass") != null
                            && logs.getDetails().get("actionClass").equals(queryLogsParams.getActionClass())))
                .filter(logs -> {
                    RequestLogUser queryUserInfo = queryLogsParams.getUserInfoParams();
                    if (queryUserInfo.getGoogleId() == null
                            && queryUserInfo.getEmail() == null
                            && queryUserInfo.getRegkey() == null) {
                        return true;
                    }
                    if (logs.getDetails() == null || logs.getDetails().get("userInfo") == null) {
                        return false;
                    }

                    Object userInfo = logs.getDetails().get("userInfo");
                    Map<String, String> userInfoMap = JsonUtils.fromJson(JsonUtils.toJson(userInfo), Map.class);
                    if (queryUserInfo.getEmail() != null
                            && !queryUserInfo.getEmail().equals(userInfoMap.get("email"))) {
                        return false;
                    }
                    if (queryUserInfo.getGoogleId() != null
                            && !queryUserInfo.getGoogleId().equals(userInfoMap.get("googleId"))) {
                        return false;
                    }
                    if (queryUserInfo.getRegkey() != null
                            && !queryUserInfo.getRegkey().equals(userInfoMap.get("regkey"))) {
                        return false;
                    }
                    return true;
                })
                .filter(logs -> queryLogsParams.getLogEvent() == null
                        || (logs.getDetails() != null
                            && queryLogsParams.getLogEvent().equals(logs.getDetails().get("event"))))
                .filter(logs -> queryLogsParams.getSourceLocation().getFile() == null
                        || logs.getSourceLocation().getFile().equals(queryLogsParams.getSourceLocation().getFile()))
                .filter(logs -> queryLogsParams.getSourceLocation().getFunction() == null
                        || logs.getSourceLocation().getFunction().equals(queryLogsParams.getSourceLocation().getFunction()))
                .filter(logs -> {
                    String latencyFilter = queryLogsParams.getLatency();
                    if (latencyFilter == null) {
                        return true;
                    }
                    if (logs.getDetails() == null) {
                        return false;
                    }
                    Object latencyObj = logs.getDetails().getOrDefault("responseTime", "NaN");
                    int logLatency;
                    try {
                        logLatency = (int) Double.parseDouble(String.valueOf(latencyObj));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    Pattern p = Pattern.compile("^(>|>=|<|<=) *(\\d+)$");
                    Matcher m = p.matcher(latencyFilter);
                    if (m.matches()) {
                        int time = Integer.parseInt(m.group(2));
                        switch (m.group(1)) {
                        case ">":
                            return logLatency > time;
                        case ">=":
                            return logLatency >= time;
                        case "<":
                            return logLatency < time;
                        case "<=":
                            return logLatency <= time;
                        default:
                            assert false : "Unreachable case";
                            break;
                        }
                    }
                    return false;
                })
                .filter(logs -> {
                    String statusFilter = queryLogsParams.getStatus();
                    if (statusFilter == null) {
                        return true;
                    }
                    if (logs.getDetails() == null) {
                        return false;
                    }
                    Object logStatusObj = logs.getDetails().getOrDefault("responseStatus", "NaN");
                    try {
                        int logStatus = (int) Double.parseDouble(String.valueOf(logStatusObj));
                        return statusFilter.equals(String.valueOf(logStatus));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .filter(logs -> queryLogsParams.getExceptionClass() == null
                        || (logs.getDetails() != null
                        && queryLogsParams.getExceptionClass().equals(logs.getDetails().get("exceptionClass"))))
                .limit(pageSize)
                .collect(Collectors.toList());

        List<GeneralLogEntry> copiedResults = deepCopyLogEntries(result);
        boolean hasNextPage = copiedResults.size() == pageSize;

        return new QueryLogsResults(copiedResults, hasNextPage);
    }

    @Override
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) {
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId, email);
        FeedbackSessionAttributes feedbackSession = fsLogic.getFeedbackSession(fsName, courseId);

        FeedbackSessionLogEntry logEntry = new FeedbackSessionLogEntry(student, feedbackSession,
                fslType, Instant.now().toEpochMilli());
        FEEDBACK_SESSION_LOG_ENTRIES.add(logEntry);
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            long startTime, long endTime, String fsName) {
        return FEEDBACK_SESSION_LOG_ENTRIES
                .stream()
                .filter(log -> log.getFeedbackSession().getCourseId().equals(courseId))
                .filter(log -> email == null || log.getStudent().getEmail().equals(email))
                .filter(log -> fsName == null || log.getFeedbackSession().getFeedbackSessionName().equals(fsName))
                .filter(log -> log.getTimestamp() >= startTime)
                .filter(log -> log.getTimestamp() <= endTime)
                .collect(Collectors.toList());
    }

    private List<GeneralLogEntry> deepCopyLogEntries(List<GeneralLogEntry> logEntries) {
        List<GeneralLogEntry> result = new ArrayList<>();
        for (GeneralLogEntry logEntry : logEntries) {
            GeneralLogEntry copiedEntry = new GeneralLogEntry(logEntry.getLogName(), logEntry.getSeverity(),
                    logEntry.getTrace(), logEntry.getInsertId(), logEntry.getResourceIdentifier(),
                    logEntry.getSourceLocation(), logEntry.getTimestamp());
            copiedEntry.setDetails(JsonUtils.fromJson(JsonUtils.toJson(logEntry.getDetails()), Map.class));
            copiedEntry.setMessage(logEntry.getMessage());
            result.add(copiedEntry);
        }

        return result;
    }
}
