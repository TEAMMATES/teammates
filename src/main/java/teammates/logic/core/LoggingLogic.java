package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload.StringPayload;

import teammates.common.exception.LoggingServiceException;
import teammates.common.util.Config;
import teammates.common.util.Logger;
import teammates.logic.core.LoggingService.LogSearcher;

/**
 * Handles the logic related to logging.
 */
public final class LoggingLogic {

    private static LoggingLogic instance = new LoggingLogic();

    private static final Logger log = Logger.getLogger();

    private static final String FEEDBACK_SESSION_LOG_NAME = "feedback-session-logs";
    private static final String FEEDBACK_SESSION_LOG_COURSE_ID_LABEL = "courseId";
    private static final String FEEDBACK_SESSION_LOG_EMAIL_LABEL = "email";
    private static final String FEEDBACK_SESSION_LOG_NAME_LABEL = "fsName";
    private static final String FEEDBACK_SESSION_LOG_TYPE_LABEL = "fslType";

    private LoggingLogic() {
        // prevent initialization
    }

    public static LoggingLogic inst() {
        return instance;
    }

    /**
     * Creates a feedback session log.
     */
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType)
            throws LoggingServiceException {
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
        LoggingService.createLogEntry(entry);
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters.
     */
    // TODO: decide on a return data format; this will likely be determined by our API
    public void getFeedbackSessionLogs(String courseId, String email, String fsName, Instant startTime, Instant endTime)
            throws LoggingServiceException {
        if (Config.isDevServer()) {
            // Not supported in dev server
            return;
        }
        LogSearcher logSearcher = LogSearcher.builder()
                .setLogName(FEEDBACK_SESSION_LOG_NAME)
                .addLabel(FEEDBACK_SESSION_LOG_COURSE_ID_LABEL, courseId)
                .addLabel(FEEDBACK_SESSION_LOG_EMAIL_LABEL, email)
                .addLabel(FEEDBACK_SESSION_LOG_NAME_LABEL, fsName)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .build();
        List<LogEntry> logEntries = logSearcher.getLogEntries();
        // TODO: remove logging statements, return the data format instead
        for (LogEntry entry : logEntries) {
            log.info("LogEntry: " + entry.toString());
        }
    }
}
