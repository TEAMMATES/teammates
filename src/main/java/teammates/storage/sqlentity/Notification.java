package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a unique notification in the system.
 */
@Entity
@Table(name = "Notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID notificationId;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStyle style;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTargetUser targetUser;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean shown;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    /**
     * Instantiates a new notification from {@code NotificationBuilder}.
     */
    public Notification(NotificationBuilder builder) {
        this.setStartTime(builder.startTime);
        this.setEndTime(builder.endTime);
        this.setStyle(builder.style);
        this.setTargetUser(builder.targetUser);
        this.setTitle(builder.title);
        this.setMessage(builder.message);
        this.setUpdatedAt(updatedAt);
        this.shown = builder.shown;
    }

    protected Notification() {
        // required by Hibernate
    }

    @Override
    public void sanitizeForSaving() {
        this.title = SanitizationHelper.sanitizeTitle(title);
        this.message = SanitizationHelper.sanitizeForRichText(message);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("notification visible time", startTime), errors);
        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("notification expiry time", endTime), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForNotificationStartAndEnd(startTime, endTime), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationStyle(style.name()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUser.name()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationTitle(title), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationBody(message), errors);

        return errors;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public NotificationStyle getStyle() {
        return style;
    }

    public void setStyle(NotificationStyle style) {
        this.style = style;
    }

    public NotificationTargetUser getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(NotificationTargetUser targetUser) {
        this.targetUser = targetUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShown() {
        return shown;
    }

    /**
     * Sets the notification as shown to the user.
     * Only allowed to change value from false to true.
     */
    public void setShown() {
        this.shown = true;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Notification [id=" + notificationId + ", startTime=" + startTime + ", endTime=" + endTime
                + ", style=" + style + ", targetUser=" + targetUser + ", shown=" + shown + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        // Notification ID uniquely identifies a notification.
        return this.getNotificationId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            Notification otherNotification = (Notification) other;
            return Objects.equals(this.notificationId, otherNotification.getNotificationId());
        } else {
            return false;
        }
    }

    /**
     * Builder for Notification.
     */
    public static class NotificationBuilder {

        private Instant startTime;
        private Instant endTime;
        private NotificationStyle style;
        private NotificationTargetUser targetUser;
        private String title;
        private String message;
        private boolean shown;

        public NotificationBuilder(String title) {
            this.title = title;
        }

        public NotificationBuilder withStartTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public NotificationBuilder withEndTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public NotificationBuilder withStyle(NotificationStyle style) {
            this.style = style;
            return this;
        }

        public NotificationBuilder withTargetUser(NotificationTargetUser targetUser) {
            this.targetUser = targetUser;
            return this;
        }

        public NotificationBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder withShown() {
            this.shown = true;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }
    }
}
