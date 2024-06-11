package teammates.logic.external;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.QueryLogsParams;

/**
 * An interface used for logs operations such as reading/writing.
 */
public interface LogService {

    /**
     * Gets the list of logs satisfying the given criteria.
     */
    QueryLogsResults queryLogs(QueryLogsParams queryLogsParams);

    /**
     * Creates a feedback session log.
     */
    void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType);

    /**
     * Creates a feedback session log for migrated courses.
     */
    void createFeedbackSessionLog(String courseId, UUID studentId, UUID fsId, String fslType);

    /**
     * Gets the feedback session logs as filtered by the given parameters ordered by ascending timestamp.
     */
    List<FeedbackSessionLogEntry> getOrderedFeedbackSessionLogs(String courseId, String email,
            long startTime, long endTime, String fsName);
}
