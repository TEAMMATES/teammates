package teammates.sqllogic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import teammates.common.util.Logger;
import teammates.storage.sqlapi.FeedbackSessionLogsDb;
import teammates.storage.sqlentity.FeedbackSessionLog;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSessionLog
 * @see FeedbackSessionLogsDb
 */
public class FeedbackSessionLogsLogic {

    private static final Logger log = Logger.getLogger();

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
     * Creates a feedback session.
     *
     */
    public void createFeedbackSessionLogs(List<FeedbackSessionLog> logs) {
        // fslDb.createFeedbackSessionLogs(logs);
    }

    /**
     * Gets feedback session logs.
     *
     * @return null if not found.
     */
    public List<FeedbackSessionLog> getFeedbackSessionLogs(String courseId, String email,
            Instant startTime, Instant endTime, String feedbackSessionName) {
        return new ArrayList<>();
        // return fslDb.getFeedbackSessionLogs(courseId, email, startTime, endTime,
        // feedbackSessionName);
    }
}
