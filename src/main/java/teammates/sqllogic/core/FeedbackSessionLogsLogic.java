package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.FeedbackSessionLog;
import teammates.logic.entity.Student;
import teammates.storage.sqlapi.FeedbackSessionLogsDb;

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
     * Gets the feedback session log with the given id.
     */
    public FeedbackSessionLog getFeedbackSessionLog(UUID id) {
        return fslDb.getFeedbackSessionLog(id);
    }

    /**
     * Creates feedback session log.
     */
    public FeedbackSessionLog createFeedbackSessionLog(
            FeedbackSession feedbackSession, Student student,
            FeedbackSessionLogType logType, Instant timestamp) throws InvalidParametersException {
        FeedbackSessionLog fsLog = new FeedbackSessionLog(student, feedbackSession, logType, timestamp);

        validateFeedbackSessionLog(fsLog);

        return fslDb.createFeedbackSessionLog(fsLog);
    }

    /**
     * Deletes feedback session logs older than the given cutoff time.
     */
    public int deleteFeedbackSessionLogsOlderThan(Instant cutoffTime) {
        return fslDb.deleteFeedbackSessionLogsOlderThan(cutoffTime);
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

    private void validateFeedbackSessionLog(FeedbackSessionLog feedbackSessionLog) throws InvalidParametersException {
        if (!feedbackSessionLog.isValid()) {
            throw new InvalidParametersException("Invalid feedback session log: " + feedbackSessionLog.getInvalidityInfo());
        }
    }
}
