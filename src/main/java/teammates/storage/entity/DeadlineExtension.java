package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.FieldValidator;

/**
 * Represents a deadline extension entity.
 */
@Entity
@Table(name = "DeadlineExtensions")
public class DeadlineExtension extends BaseEntity {
    @Id
    private UUID id;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID userId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sessionId", nullable = false)
    private FeedbackSession feedbackSession;

    @Column(nullable = false, insertable = false, updatable = false)
    private UUID sessionId;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private boolean isClosingSoonEmailSent;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    protected DeadlineExtension() {
        // required by Hibernate
    }

    public DeadlineExtension(User user, Instant endTime) {
        this.setId(UUID.randomUUID());
        this.setUser(user);
        this.setEndTime(endTime);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    /**
     * Sets the user as well as the userId.
     */
    public void setUser(User user) {
        this.user = user;
        this.userId = user == null ? null : user.getId();
    }

    public FeedbackSession getFeedbackSession() {
        return feedbackSession;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    /**
     * Sets the feedback session of the deadline extension.
     */
    public void setFeedbackSession(FeedbackSession feedbackSession) {
        this.feedbackSession = feedbackSession;
        this.sessionId = feedbackSession == null ? null : feedbackSession.getId();
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public boolean isClosingSoonEmailSent() {
        return isClosingSoonEmailSent;
    }

    public void setClosingSoonEmailSent(boolean isClosingSoonEmailSent) {
        this.isClosingSoonEmailSent = isClosingSoonEmailSent;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "DeadlineExtension [id=" + id + ", user=" + user + ", feedbackSessionId=" + feedbackSession.getId()
                + ", endTime=" + endTime + ", isClosingSoonEmailSent=" + isClosingSoonEmailSent
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DeadlineExtension other)) {
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

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
                feedbackSession.getEndTime(), Set.of(this)), errors);

        return errors;
    }
}
