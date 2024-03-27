package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;

import teammates.storage.sqlapi.FeedbackSessionLogsDb;
import teammates.storage.sqlentity.FeedbackSessionLog;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSessionLog
 * @see FeedbackSessionLogsDb
 */
public final class FeedbackSessionLogsLogic {

    private static final FeedbackSessionLogsLogic instance = new FeedbackSessionLogsLogic();

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
    public void createFeedbackSessionLogs(List<FeedbackSessionLog> logs) {
        for (FeedbackSessionLog log : logs) {
            fslDb.createFeedbackSessionLog(log);
        }
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters ordered by
     * ascending timestamp. Logs with the same timestamp will be ordered by the
     * student's email.
     *
     * @param studentEmail        Can be null
     * @param feedbackSessionName Can be null
     */
    public List<FeedbackSessionLog> getOrderedFeedbackSessionLogs(String courseId, String studentEmail,
            String feedbackSessionName, Instant startTime, Instant endTime) {
        return fslDb.getOrderedFeedbackSessionLogs(courseId, studentEmail, feedbackSessionName, startTime,
                endTime);
    }
}
