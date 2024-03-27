package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, Map<String, Map<String, Map<String, Long>>>> lastSavedTimestamps = new HashMap<>();
        Map<String, Map<String, Student>> studentsMap = new HashMap<>();
        Map<String, Map<String, FeedbackSession>> sessionsMap = new HashMap<>();
        for (FeedbackSessionLogEntry logEntry : logEntries) {

            if (!isCourseMigrated(logEntry.getCourseId())) {
                continue;
            }

            String courseId = logEntry.getCourseId();
            String email = logEntry.getStudentEmail();
            String fbSessionName = logEntry.getFeedbackSessionName();
            String type = logEntry.getFeedbackSessionLogType();
            Long timestamp = logEntry.getTimestamp();

            studentsMap.computeIfAbsent(courseId, k -> new HashMap<>());
            sessionsMap.computeIfAbsent(courseId, k -> new HashMap<>());

            lastSavedTimestamps.computeIfAbsent(email, k -> new HashMap<>());
            lastSavedTimestamps.get(email).computeIfAbsent(courseId, k -> new HashMap<>());
            lastSavedTimestamps.get(email).get(courseId).computeIfAbsent(fbSessionName, k -> new HashMap<>());
            Long lastSaved = lastSavedTimestamps.get(email).get(courseId).get(fbSessionName).getOrDefault(type, 0L);

            if (Math.abs(timestamp - lastSaved) > SPAM_FILTER) {
                lastSavedTimestamps.get(email).get(courseId).get(fbSessionName).put(type, timestamp);
                Student student = studentsMap.get(courseId).computeIfAbsent(email,
                        k -> sqlLogic.getStudentForEmail(courseId, email));
                FeedbackSession feedbackSession = sessionsMap.get(courseId).computeIfAbsent(fbSessionName,
                        k -> sqlLogic.getFeedbackSession(fbSessionName, courseId));
                FeedbackSessionLog fslEntity = new FeedbackSessionLog(student, feedbackSession,
                        FeedbackSessionLogType.valueOfLabel(type), Instant.ofEpochMilli(timestamp));
                filteredLogs.add(fslEntity);
            }
        }

        sqlLogic.createFeedbackSessionLogs(filteredLogs);

        return new JsonResult("Successful");
    }
}
