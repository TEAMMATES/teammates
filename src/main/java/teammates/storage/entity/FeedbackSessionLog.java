package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;

/**
 * Represents a feedback session log.
 */
@Entity
@Table(name = "FeedbackSessionLogs")
public class FeedbackSessionLog extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID studentId;

    @ManyToOne
    @JoinColumn(name = "sessionId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeedbackSession feedbackSession;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID sessionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackSessionLogType feedbackSessionLogType;

    @Column(nullable = false)
    private Instant timestamp;

    protected FeedbackSessionLog() {
        // required by Hibernate
    }

    public FeedbackSessionLog(Student student, FeedbackSession feedbackSession,
            FeedbackSessionLogType feedbackSessionLogType, Instant timestamp) {
        this.setId(UUID.randomUUID());
        this.setStudent(student);
        this.setFeedbackSession(feedbackSession);
        this.setFeedbackSessionLogType(feedbackSessionLogType);
        this.setTimestamp(timestamp);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public UUID getStudentId() {
        return studentId;
    }

    /**
     * Sets the student of the feedback session log.
     */
    public void setStudent(Student student) {
        this.student = student;
        this.studentId = student == null ? null : student.getId();
    }

    public FeedbackSession getFeedbackSession() {
        return feedbackSession;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    /**
     * Sets the feedback session of the feedback session log.
     */
    public void setFeedbackSession(FeedbackSession feedbackSession) {
        this.feedbackSession = feedbackSession;
        this.sessionId = feedbackSession == null ? null : feedbackSession.getId();
    }

    public FeedbackSessionLogType getFeedbackSessionLogType() {
        return feedbackSessionLogType;
    }

    public void setFeedbackSessionLogType(FeedbackSessionLogType feedbackSessionLogType) {
        this.feedbackSessionLogType = feedbackSessionLogType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FeedbackSessionLog [id=" + id + ", student=" + student + ", feedbackSession=" + feedbackSession
                + ", feedbackSessionLogType=" + feedbackSessionLogType.getLabel() + ", timestamp=" + timestamp + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FeedbackSessionLog other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        if (this.student == null) {
            errors.add("Student for feedback session log does not exist");
        }

        if (this.feedbackSession == null) {
            errors.add("Feedback session for feedback session log does not exist");
        }

        if (this.timestamp == null) {
            errors.add("Timestamp for feedback session log does not exist");
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        if (!Objects.equals(this.student.getCourseId(), this.feedbackSession.getCourseId())) {
            errors.add("Student and feedback session for feedback session log do not belong to the same course");
        }

        return errors;
    }
}
