package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents a log entry of a feedback session.
 */
public class FeedbackSessionLogEntry {
    private final StudentAttributes student;
    private final FeedbackSessionAttributes feedbackSession;
    private final String feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogEntry(StudentAttributes student, FeedbackSessionAttributes feedbackSession,
            String feedbackSessionLogType, long timestamp) {
        this.student = student;
        this.feedbackSession = feedbackSession;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public StudentAttributes getStudent() {
        return this.student;
    }

    public FeedbackSessionAttributes getFeedbackSession() {
        return this.feedbackSession;
    }

    public String getFeedbackSessionLogType() {
        return this.feedbackSessionLogType;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
