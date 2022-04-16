package teammates.ui.output;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;

/**
 * The API output format of a notification.
 */
public class NotificationData extends ApiOutput {

    private String notificationId;
    private long startTimestamp;
    private long endTimestamp;
    private long createdAt;
    private NotificationStyle style;
    private NotificationTargetUser targetUser;
    private String title;
    private String message;
    private boolean shown;

    public NotificationData(NotificationAttributes notificationAttributes) {
        this.notificationId = notificationAttributes.getNotificationId();
        this.startTimestamp = notificationAttributes.getStartTime().toEpochMilli();
        this.endTimestamp = notificationAttributes.getEndTime().toEpochMilli();
        this.createdAt = notificationAttributes.getCreatedAt().toEpochMilli();
        this.style = notificationAttributes.getStyle();
        this.targetUser = notificationAttributes.getTargetUser();
        this.title = notificationAttributes.getTitle();
        this.message = notificationAttributes.getMessage();
        this.shown = notificationAttributes.isShown();
    }

    public String getNotificationId() {
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
