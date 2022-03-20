package teammates.ui.output;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.common.datatransfer.attributes.NotificationAttributes;

/**
 * The API output format of a notification.
 */
public class NotificationData extends ApiOutput {

    private String notificationId;
    private long startTimestamp;
    private long endTimestamp;
    private Long createdAt;
    private Long updatedAt;
    private NotificationType notificationType;
    private NotificationTargetUser targetUser;
    private String title;
    private String message;
    private boolean shown;

    public NotificationData(NotificationAttributes notificationAttributes) {
        this.notificationId = notificationAttributes.getNotificationId();
        this.startTimestamp = notificationAttributes.getStartTime().toEpochMilli();
        this.endTimestamp = notificationAttributes.getStartTime().toEpochMilli();
        this.createdAt = notificationAttributes.getCreatedAt().toEpochMilli();
        this.updatedAt = notificationAttributes.getUpdatedAt().toEpochMilli();
        this.notificationType = notificationAttributes.getType();
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

    public Long getCreatedAt() {
        return this.createdAt;
    }

    public Long getUpdatedAt() {
        return this.updatedAt;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
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

    /**
     * Hides some attributes to instructor and students without appropriate privilege.
     */
    public void hideInformationForNonAdmin() {
        // TODO: hide information that might be admin-only: target users, isShown, (starttime and endtime)
    }
}
