package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.ObjectNotFoundException;

import teammates.common.util.Logger;
import teammates.storage.sqlapi.FeedbackSessionLogsDb;
import teammates.storage.sqlentity.FeedbackSessionLog;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSessionLog
 * @see FeedbackSessionLogsDb
 */
public final class FeedbackSessionLogsLogic {

    private static final Logger log = Logger.getLogger();

    private static final FeedbackSessionLogsLogic instance = new FeedbackSessionLogsLogic();

    private static final String ERROR_FAILED_TO_CREATE_LOG = "Failed to create session activity log";

    private FeedbackSessionLogsDb fslDb;

    private FeedbackSessionLogsLogic() {
        // prevent initialization
    }

    public static FeedbackSessionLogsLogic inst() {
        return instance;
    }

    void initLogicDependencies(FeedbackSessionLogsDb fslDb) {
        this.fslDb = fslDb;
    }

    /**
     * Creates feedback session logs.
     */
    public void createFeedbackSessionLogs(List<FeedbackSessionLog> fsLogs) {
        for (FeedbackSessionLog fsLog : fsLogs) {
            try {
                fslDb.createFeedbackSessionLog(fsLog);
            } catch (ObjectNotFoundException e) {
                log.severe(String.format(ERROR_FAILED_TO_CREATE_LOG), e);
            }
        }
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters ordered by
     * ascending timestamp. Logs with the same timestamp will be ordered by the
     * student's email.
     *
     * @param studentId        Can be null
     * @param feedbackSessionId Can be null
     */
    public List<FeedbackSessionLog> getOrderedFeedbackSessionLogs(String courseId, UUID studentId,
            UUID feedbackSessionId, Instant startTime, Instant endTime) {
        return fslDb.getOrderedFeedbackSessionLogs(courseId, studentId, feedbackSessionId, startTime,
                endTime);
    }
}
