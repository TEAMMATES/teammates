package teammates.ui.output;

import java.util.UUID;

import org.threeten.bp.Instant;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.logic.entity.Notification;

/**
 * The API output format of a notification.
 */
public class NotificationData extends ApiOutput {

    private UUID notificationId;
    private long startTimestamp;
    private long endTimestamp;
    private long createdAt;
    private NotificationStyle style;
    private NotificationTargetUser targetUser;
    private String title;
    private String message;
    private boolean shown;

    private NotificationData() {
        // for Jackson deserialization
    }

    public NotificationData(Notification notification) {
        this.notificationId = notification.getId();
        this.startTimestamp = notification.getStartTime().toEpochMilli();
        this.endTimestamp = notification.getEndTime().toEpochMilli();
        this.createdAt = notification.getCreatedAt() == null
                ? Instant.now().toEpochMilli() : notification.getCreatedAt().toEpochMilli();
        this.style = notification.getStyle();
        this.targetUser = notification.getTargetUser();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.shown = notification.isShown();
    }

    public UUID getNotificationId() {
        return this.notificationId;
    }

    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    public long getEndTimestamp() {
        return this.endTimestamp;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public NotificationStyle getStyle() {
        return this.style;
    }

    public NotificationTargetUser getTargetUser() {
        return this.targetUser;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isShown() {
        return this.shown;
    }
}
