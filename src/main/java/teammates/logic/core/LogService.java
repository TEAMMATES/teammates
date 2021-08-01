package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.datatransfer.logs.QueryLogsParams;
import teammates.common.exception.LogServiceException;

/**
 * An interface used for logs operations such as reading/writing.
 */
public interface LogService {

    /**
     * Gets the list of recent error- or higher level logs.
     */
    List<ErrorLogEntry> getRecentErrorLogs();

    /**
     * Gets the list of logs satisfying the given criteria.
     */
    QueryLogsResults queryLogs(QueryLogsParams queryLogsParams) throws LogServiceException;

    /**
     * Creates a feedback session log.
     */
    @Deprecated
    void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) throws LogServiceException;

    /**
     * Gets the feedback session logs as filtered by the given parameters.
     */
    List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName)
            throws LogServiceException;
}
