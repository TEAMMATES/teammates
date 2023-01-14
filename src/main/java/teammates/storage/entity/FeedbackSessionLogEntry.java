package teammates.storage.entity;

import java.time.Instant;
import java.util.UUID;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;

/**
 * Represents a log entry of a feedback session.
 */
@Entity
@Index
public class FeedbackSessionLogEntry extends BaseEntity {

    @Id
    private String feedbackSessionLogEntryId;

    private String studentEmail;

    private String courseId;

    private String feedbackSessionName;

    private String feedbackSessionLogType;

    private String remarks;

    private long timestamp;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @SuppressWarnings("unused")
    private FeedbackSessionLogEntry() {
        // required by Objectify
    }

    public FeedbackSessionLogEntry(String studentEmail, String courseId, String feedbackSessionName,
            String feedbackSessionLogType, long timestamp) {
        this.feedbackSessionLogEntryId = UUID.randomUUID().toString();
        this.setStudentEmail(studentEmail);
        this.setCourseId(courseId);
        this.setFeedbackSessionName(feedbackSessionName);
        this.setFeedbackSessionLogType(feedbackSessionLogType);
        this.setTimestamp(timestamp);
        this.setCreatedAt(Instant.now());
    }

    public String getFeedbackSessionLogEntryId() {
        return feedbackSessionLogEntryId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getFeedbackSessionLogType() {
        return this.feedbackSessionLogType;
    }

    public void setFeedbackSessionLogType(String feedbackSessionLogType) {
        this.feedbackSessionLogType = feedbackSessionLogType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
