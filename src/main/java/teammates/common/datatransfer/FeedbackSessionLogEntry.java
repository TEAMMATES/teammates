package teammates.common.datatransfer;

import java.util.UUID;

/**
 * Represents a log entry of a feedback session.
 */
public class FeedbackSessionLogEntry implements Comparable<FeedbackSessionLogEntry> {
    private final String courseId;
    private final UUID studentId;
    private final String studentEmail;
    private final UUID feedbackSessionId;
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

    public FeedbackSessionLogEntry(String courseId, UUID studentId, UUID feedbackSessionId,
            String feedbackSessionLogType, long timestamp) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.studentEmail = null;
        this.feedbackSessionId = feedbackSessionId;
        this.feedbackSessionName = null;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
    }

    public String getCourseId() {
        return courseId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public UUID getFeedbackSessionId() {
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
