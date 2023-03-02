package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import teammates.common.util.FieldValidator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a log entry of a feedback session.
 */
@Entity
@Table(name = "FeedbackSessionLogEntries")
public class FeedbackSessionLogEntry extends BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String studentEmail;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false)
    private String feedbackSessionName;

    @Column(nullable = false)
    private String feedbackSessionLogType;

    @Column(nullable = false)
    private Integer windowSize;

    @Column(nullable = false)
    private Long timestamp;

    protected FeedbackSessionLogEntry() {
        // required by Hibernate
    }

    public FeedbackSessionLogEntry(
            String studentEmail, String courseId, String feedbackSessionName,
            String feedbackSessionLogType, long timestamp) {
        this.setStudentEmail(studentEmail);
        this.setCourseId(courseId);
        this.setFeedbackSessionName(feedbackSessionName);
        this.setFeedbackSessionLogType(feedbackSessionLogType);
        this.setTimestamp(timestamp);
        this.setCreatedAt(Instant.now());
        this.setWindowSize(1);
    }

    public Integer getId() {
        return id;
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

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        // Check for null fields.
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME, feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField(
                FieldValidator.COURSE_ID_FIELD_NAME, courseId), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("student's email", studentEmail), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(studentEmail), errors);

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        return errors;
    }

}
