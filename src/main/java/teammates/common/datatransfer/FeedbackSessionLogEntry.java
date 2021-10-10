package teammates.common.datatransfer;

/**
 * Represents a log entry of a feedback session.
 */
public class FeedbackSessionLogEntry {
    private final String studentEmail;
    private final String feedbackSessionName;
    private final String feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogEntry(String studentEmail, String feedbackSessionName,
            String feedbackSessionLogType, long timestamp) {
        this.studentEmail = studentEmail;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getFeedbackSessionLogType() {
        return this.feedbackSessionLogType;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
