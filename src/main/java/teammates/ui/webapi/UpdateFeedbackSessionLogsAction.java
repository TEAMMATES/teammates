package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * Process feedback session logs from GCP in the past defined time period and
 * store in the database.
 */
public class UpdateFeedbackSessionLogsAction extends AdminOnlyAction {

    static final int COLLECTION_TIME_PERIOD = 60; // represents one hour
    static final long SPAM_FILTER = 2000L; // in ms

    @Override
    public JsonResult execute() {
        List<FeedbackSessionLog> filteredLogs = new ArrayList<>();

        Instant endTime = TimeHelper.getInstantNearestHourBefore(Instant.now());
        Instant startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);

        List<FeedbackSessionLogEntry> logEntries = logsProcessor.getOrderedFeedbackSessionLogs(null, null,
                startTime.toEpochMilli(), endTime.toEpochMilli(), null);

        Map<UUID, Map<String, Map<UUID, Map<String, Long>>>> lastSavedTimestamps = new HashMap<>();
        Map<String, Map<UUID, Student>> studentsMap = new HashMap<>();
        Map<String, Map<UUID, FeedbackSession>> sessionsMap = new HashMap<>();
        for (FeedbackSessionLogEntry logEntry : logEntries) {

            if (!isCourseMigrated(logEntry.getCourseId())) {
                continue;
            }

            String courseId = logEntry.getCourseId();
            UUID studentId = logEntry.getStudentId();
            UUID fbSessionId = logEntry.getFeedbackSessionId();
            String type = logEntry.getFeedbackSessionLogType();
            Long timestamp = logEntry.getTimestamp();

            studentsMap.computeIfAbsent(courseId, k -> new HashMap<>());
            sessionsMap.computeIfAbsent(courseId, k -> new HashMap<>());

            lastSavedTimestamps.computeIfAbsent(studentId, k -> new HashMap<>());
            lastSavedTimestamps.get(studentId).computeIfAbsent(courseId, k -> new HashMap<>());
            lastSavedTimestamps.get(studentId).get(courseId).computeIfAbsent(fbSessionId, k -> new HashMap<>());
            Long lastSaved = lastSavedTimestamps.get(studentId).get(courseId).get(fbSessionId).getOrDefault(type, 0L);

            if (Math.abs(timestamp - lastSaved) > SPAM_FILTER) {
                lastSavedTimestamps.get(studentId).get(courseId).get(fbSessionId).put(type, timestamp);
                Student student = studentsMap.get(courseId).computeIfAbsent(studentId,
                        k -> sqlLogic.getStudent(studentId));
                FeedbackSession feedbackSession = sessionsMap.get(courseId).computeIfAbsent(fbSessionId,
                        k -> sqlLogic.getFeedbackSession(fbSessionId));
                FeedbackSessionLog fslEntity = new FeedbackSessionLog(student, feedbackSession,
                        FeedbackSessionLogType.valueOfLabel(type), Instant.ofEpochMilli(timestamp));
                filteredLogs.add(fslEntity);
            }
        }

        sqlLogic.createFeedbackSessionLogs(filteredLogs);

        return new JsonResult("Successful");
    }
}
