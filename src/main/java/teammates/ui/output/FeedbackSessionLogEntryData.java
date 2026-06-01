package teammates.ui.output;

import java.util.UUID;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.User;

/**
 * The session log of a student for a single feedback session.
 */
public class FeedbackSessionLogEntryData {
    private final UUID feedbackSessionLogEntryId;
    private final UserData user;
    private final FeedbackSessionLogType feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogEntryData(FeedbackSessionLog logEntry, User user) {
        this.feedbackSessionLogEntryId = logEntry.getId();
        this.user = new UserData(user);
        this.feedbackSessionLogType = logEntry.getFeedbackSessionLogType();
        this.timestamp = logEntry.getTimestamp().toEpochMilli();
    }

    public UUID getFeedbackSessionLogEntryId() {
        return feedbackSessionLogEntryId;
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
