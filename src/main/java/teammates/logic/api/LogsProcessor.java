package teammates.logic.api;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.exception.LogServiceException;
import teammates.common.util.Config;
import teammates.logic.core.GoogleCloudLoggingService;
import teammates.logic.core.LocalLoggingService;
import teammates.logic.core.LogService;

/**
 * Handles operations related to logs reading/writing.
 *
 * <p>Note that while this interface should support writing logs, most of the application/system logs
 * should be written via the standard Logger class.
 */
public class LogsProcessor {

    private final LogService service;

    public LogsProcessor() {
        if (Config.isDevServer()) {
            service = new LocalLoggingService();
        } else {
            service = new GoogleCloudLoggingService();
        }
    }

    /**
     * Gets the list of recent error- or higher level logs.
     */
    public List<ErrorLogEntry> getRecentErrorLogs() {
        return service.getRecentErrorLogs();
    }

    /**
     * Creates a feedback session log.
     */
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType)
            throws LogServiceException {
        service.createFeedbackSessionLog(courseId, email, fsName, fslType);
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters.
     * @param email Can be null
     */
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName) throws LogServiceException {
        return service.getFeedbackSessionLogs(courseId, email, startTime, endTime, fsName);
    }

}
