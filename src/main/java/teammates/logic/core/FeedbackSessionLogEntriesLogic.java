package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;

import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackSessionLogEntriesDb;

/**
 * Handles the logic related to feedback session log entries
 */
public final class FeedbackSessionLogEntriesLogic {
    private static final FeedbackSessionLogEntriesLogic instance
            = new FeedbackSessionLogEntriesLogic();

    private final FeedbackSessionLogEntriesDb fslEntriesDb
            = FeedbackSessionLogEntriesDb.inst();

    private FeedbackSessionLogEntriesLogic() {
        // prevent initialization
    }

    public static FeedbackSessionLogEntriesLogic inst() {
        return instance;
    }

    public List<FeedbackSessionLogEntryAttributes> getFeedbackSessionLogs(String courseId, String email,
                                                                long startTime, long endTime, String fsName) {
        return fslEntriesDb.getFeedbackSessionLogs(courseId, email, startTime, endTime, fsName);
    }

    public long getLatestLogTimestamp() {
        return fslEntriesDb.getLatestLogTimestamp();
    }

    public List<FeedbackSessionLogEntryAttributes> createFeedbackSessionLogs(
            List<FeedbackSessionLogEntryAttributes> entries) throws InvalidParametersException {
        return fslEntriesDb.createFeedbackSessionLogs(entries);
    }
}
