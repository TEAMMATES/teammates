package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.cloud.logging.Severity;

import teammates.common.util.Config;
import teammates.common.util.Logger;
import teammates.logic.core.LoggingService.LogSearcher;

/**
 * Handles the logic related to logging.
 */
public final class LoggingLogic {

    private static LoggingLogic instance = new LoggingLogic();

    private static final Logger log = Logger.getLogger();

    private static final String LOG_NAME = "audit-logs";
    private static final Severity LOG_LEVEL = Severity.INFO;
    private static final String LOG_TYPE_ACCESS = "access";
    private static final String LOG_TYPE_SUBMIT = "submit";

    private LoggingLogic() {
        // prevent initialization
    }

    public static LoggingLogic inst() {
        return instance;
    }

    /**
     * Creates a feedback session log.
     */
    public void createFeedbackSessionLog(String courseId, String email, boolean isAccess) {
        if (Config.isDevServer()) {
            // Not supported in dev server
            return;
        }
        String payload = "Feedback session log: courseId=" + courseId + " email=" + email;
        String type = isAccess ? LOG_TYPE_ACCESS : LOG_TYPE_SUBMIT;
        LogEntry entry = LogEntry.newBuilder(StringPayload.of(payload))
                .setLogName(LOG_NAME)
                .setSeverity(LOG_LEVEL)
                .addLabel("type", type)
                .addLabel("courseId", courseId)
                .addLabel("email", email)
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();
        LoggingService.createLogEntry(entry);
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters.
     */
    // TODO: decide on a return data format; it will likely be determined by our API
    public void getFeedbackSessionLogs(String courseId, String email, Instant startTime, Instant endTime) {
        if (Config.isDevServer()) {
            // Not supported in dev server
            return;
        }
        LogSearcher logSearcher = LogSearcher.builder()
                .setLogName(LOG_NAME)
                .setSeverity(LOG_LEVEL)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .addLabel("courseId", courseId)
                .addLabel("email", email)
                .build();
        List<LogEntry> logEntries = logSearcher.getLogEntries();
        // TODO: remove logging statements, return the data format instead
        for (LogEntry entry : logEntries) {
            log.info("LogEntry: " + entry.toString());
        }
    }
}
