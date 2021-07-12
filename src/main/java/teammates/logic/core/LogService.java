package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.GeneralLogEntry.SourceLocation;
import teammates.common.datatransfer.QueryLogsResults;
import teammates.common.exception.LogServiceException;

/**
 * An interface used for logs operations such as reading/writing.
 */
public interface LogService {

    List<ErrorLogEntry> getRecentErrorLogs();

    QueryLogsResults queryLogs(String severity, String minSeverity, Instant startTime, Instant endTime,
            String traceId, String apiEndpoint, String userId, String logEvent, SourceLocation sourceLocation,
            String exceptionClass, Integer pageSize, String pageToken) throws LogServiceException;

    void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType) throws LogServiceException;

    List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName)
            throws LogServiceException;
}
