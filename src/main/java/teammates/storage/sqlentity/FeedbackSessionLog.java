package teammates.storage.sqlentity;

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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
    @JoinColumn(name = "studentId")
    @NotFound(action = NotFoundAction.IGNORE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "sessionId")
    @NotFound(action = NotFoundAction.IGNORE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeedbackSession feedbackSession;

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
        this.student = student;
        this.feedbackSession = feedbackSession;
        this.feedbackSessionLogType = feedbackSessionLogType;
        this.timestamp = timestamp;
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

    public void setStudent(Student student) {
        this.student = student;
    }

    public FeedbackSession getFeedbackSession() {
        return feedbackSession;
    }

    public void setFeedbackSession(FeedbackSession feedbackSession) {
        this.feedbackSession = feedbackSession;
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
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackSessionLog otherFeedbackSessionLog = (FeedbackSessionLog) other;
            return Objects.equals(this.getId(), otherFeedbackSessionLog.getId());
        } else {
            return false;
        }
    }

    @Override
    public List<String> getInvalidityInfo() {
        return new ArrayList<>();
    }
}
