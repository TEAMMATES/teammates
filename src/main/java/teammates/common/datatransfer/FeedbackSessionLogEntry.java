package teammates.common.datatransfer;

/**
 * Represents a log entry of a feedback session.
 */
public class FeedbackSessionLogEntry implements Comparable<FeedbackSessionLogEntry> {
    private final String courseId;
    private final String studentId;
    private final String studentEmail;
    private final String feedbackSessionId;
    private final String feedbackSessionName;
    private final String feedbackSessionLogType;
    private final long timestamp;

    public FeedbackSessionLogEntry(String courseId, String studentEmail,
            String feedbackSessionName, String feedbackSessionLogType, long timestamp) {
        this.courseId = courseId;
        this.studentId = null;
        this.studentEmail = studentEmail;
        this.feedbackSessionId = null;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public FeedbackSessionLogEntry(String courseId, String studentId, String studentEmail, String feedbackSessionId,
            String feedbackSessionName, String feedbackSessionLogType, long timestamp) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.studentEmail = studentEmail;
        this.feedbackSessionId = feedbackSessionId;
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getFeedbackSessionId() {
        return feedbackSessionId;
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

    @Override
    public int compareTo(FeedbackSessionLogEntry o) {
        return Long.compare(this.getTimestamp(), o.getTimestamp());
    }
}
