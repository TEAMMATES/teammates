package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload.StringPayload;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.LogsServiceException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.core.LogsService.LogSearcher;

/**
 * Handles the logic related to logging.
 */
public final class LogsLogic {

    private static LogsLogic instance = new LogsLogic();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    private static final String FEEDBACK_SESSION_LOG_NAME = "feedback-session-logs";
    private static final String FEEDBACK_SESSION_LOG_COURSE_ID_LABEL = "courseId";
    private static final String FEEDBACK_SESSION_LOG_EMAIL_LABEL = "email";
    private static final String FEEDBACK_SESSION_LOG_NAME_LABEL = "fsName";
    private static final String FEEDBACK_SESSION_LOG_TYPE_LABEL = "fslType";

    private LogsLogic() {
        // prevent initialization
    }

    public static LogsLogic inst() {
        return instance;
    }

    /**
     * Creates a feedback session log.
     */
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType)
            throws LogsServiceException {
        if (Config.isDevServer()) {
            // Not supported in dev server
            return;
        }

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
        LogsService.createLogEntry(entry);
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters.
     * @param email Can be null
     */
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime) throws LogsServiceException {
        if (Config.isDevServer()) {
            // Not supported in dev server
            return new ArrayList<>();
        }

        LogSearcher logSearcher = LogSearcher.builder()
                .setLogName(FEEDBACK_SESSION_LOG_NAME)
                .addLabel(FEEDBACK_SESSION_LOG_COURSE_ID_LABEL, courseId)
                .addLabel(FEEDBACK_SESSION_LOG_EMAIL_LABEL, email)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .build();
        List<LogEntry> logEntries = logSearcher.getLogEntries();

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
                    && !fslType.equals(Const.FeedbackSessionLogTypes.SUBMISSION)) {
                // If the feedback session log type retrieved from the logs is invalid, discard it
                continue;
            }
            FeedbackSessionLogEntry fslEntry = new FeedbackSessionLogEntry(student, fs, fslType, timestamp);
            fsLogEntries.add(fslEntry);
        }

        return fsLogEntries;
    }
}
