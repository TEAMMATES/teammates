package teammates.ui.output;

import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.storage.entity.FeedbackSessionLog;

/**
 * The session log of a student for a single feedback session.
 */
public class FeedbackSessionLogData {
    private final UUID feedbackSessionLogId;
    private final UserData user;
    private final FeedbackSessionLogType feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogData(FeedbackSessionLog logEntry) {
        this.feedbackSessionLogId = logEntry.getId();
        this.user = new UserData(logEntry.getUser());
        this.feedbackSessionLogType = logEntry.getFeedbackSessionLogType();
        this.timestamp = logEntry.getTimestamp().toEpochMilli();
    }

    public UUID getFeedbackSessionLogId() {
        return feedbackSessionLogId;
    }

    public UserData getUser() {
        return user;
    }

    public FeedbackSessionLogType getFeedbackSessionLogType() {
        return feedbackSessionLogType;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
