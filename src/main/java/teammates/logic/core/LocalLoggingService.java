package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.ErrorLogEntry;
import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.exception.LogServiceException;

/**
 * Holds functions for operations related to logs reading/writing in local dev environment.
 *
 * <p>It is expected that most the implementation will be no-op or returning null/empty list
 * as there is no logs retention locally.
 *
 * <p>Writing a local logs ingestion service is possible, but is an overkill at this point of time.
 */
public class LocalLoggingService implements LogService {

    @Override
    public List<ErrorLogEntry> getRecentErrorLogs() {
        // Not supported in dev server
        return new ArrayList<>();
    }

    @Override
    public void createFeedbackSessionLog(String courseId, String email, String fsName, String fslType)
            throws LogServiceException {
        // Not supported in dev server
    }

    @Override
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String fsName) throws LogServiceException {
        // Not supported in dev server
        return new ArrayList<>();
    }

}
