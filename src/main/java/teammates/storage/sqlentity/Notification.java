package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a unique notification in the system.
 */
@Entity
@Table(name = "Notifications")
public class Notification extends BaseEntity {

    @Id
    private UUID id;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private boolean shown;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ReadNotification> readNotifications = new ArrayList<>();

    /**
     * Instantiates a new notification.
     */
    public Notification(Instant startTime, Instant endTime, NotificationStyle style,
            NotificationTargetUser targetUser, String title, String message) {
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setStyle(style);
        this.setTargetUser(targetUser);
        this.setTitle(title);
        this.setMessage(message);
        this.setId(UUID.randomUUID());
    }

    protected Notification() {
        // required by Hibernate
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        this.title = SanitizationHelper.sanitizeTitle(title);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = SanitizationHelper.sanitizeForRichText(message);
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ReadNotification> getReadNotifications() {
        return readNotifications;
    }

    public void setReadNotifications(List<ReadNotification> readNotifications) {
        this.readNotifications = readNotifications;
    }

    @Override
    public String toString() {
        return "Notification [notificationId=" + id + ", startTime=" + startTime + ", endTime=" + endTime
                + ", style=" + style + ", targetUser=" + targetUser + ", title=" + title + ", message=" + message
                + ", shown=" + shown + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt
                + ", readNotifications=" + readNotifications + "]";
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
            Notification otherNotification = (Notification) other;
            return Objects.equals(this.getId(), otherNotification.getId());
        } else {
            return false;
        }
    }
}
