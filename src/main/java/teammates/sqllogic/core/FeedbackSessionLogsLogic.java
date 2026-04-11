package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackSessionLogsDb;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

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
    public void createFeedbackSessionLogs(List<FeedbackSessionLog> fsLogs) throws InvalidParametersException {
        if (fsLogs == null) {
            throw new InvalidParametersException("Feedback session logs list does not exist");
        }

        for (FeedbackSessionLog fsLog : fsLogs) {
            createFeedbackSessionLog(fsLog);
        }
    }

    /**
     * Creates feedback session log.
     */
    public void createFeedbackSessionLog(FeedbackSessionLog fsLog) throws InvalidParametersException {
        if (fsLog == null) {
            throw new InvalidParametersException("Feedback session log does not exist");
        }
        validateFeedbackSessionLogContext(fsLog.getStudent(), fsLog.getFeedbackSession());
        fslDb.createFeedbackSessionLog(fsLog);
    }

    /**
     * Deletes feedback session logs older than the given cutoff time.
     */
    public int deleteFeedbackSessionLogsOlderThan(Instant cutoffTime) {
        return fslDb.deleteFeedbackSessionLogsOlderThan(cutoffTime);
    }

    /**
     * Validates that feedback session log entities belong to the same course.
     */
    private void validateFeedbackSessionLogContext(Student student, FeedbackSession feedbackSession)
            throws InvalidParametersException {
        if (student == null) {
            throw new InvalidParametersException("Student for feedback session log does not exist");
        }
        if (feedbackSession == null) {
            throw new InvalidParametersException("Feedback session for feedback session log does not exist");
        }

        String studentCourseId = student.getCourse().getId();
        String feedbackSessionCourseId = feedbackSession.getCourse().getId();
        if (!Objects.equals(studentCourseId, feedbackSessionCourseId)) {
            throw new InvalidParametersException("Student and feedback session belong to different courses");
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
