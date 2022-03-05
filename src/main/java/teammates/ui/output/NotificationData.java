package teammates.ui.output;

import teammates.common.datatransfer.attributes.NotificationAttributes;

/**
 * The API output format of a notification.
 */
public class NotificationData extends ApiOutput {

    private String notificationId;
    private Long startTimestamp;
    private Long endTimestamp;
    private String type;
    private String targetUser;
    private String title;
    private String message;
    private boolean shown;

    public NotificationData(NotificationAttributes notificationAttributes) {
        this.notificationId = notificationAttributes.getNotificationId();
        this.startTimestamp = notificationAttributes.getStartTime().toEpochMilli();
        this.endTimestamp = notificationAttributes.getStartTime().toEpochMilli();
        this.type = notificationAttributes.getType();
        this.targetUser = notificationAttributes.getTargetUser();
        this.title = notificationAttributes.getTitle();
        this.message = notificationAttributes.getMessage();
        this.shown = notificationAttributes.isShown();

    }

    public String getNotificationId() {
        return this.notificationId;
    }

    public Long getStartTimestamp() {
        return this.startTimestamp;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    public String getNotificationType() {
        return this.type;
    }

    public String getTargetUser() {
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
        // TODO: hide informations that might be admin-only: targer users, isShown, (starttime and endtime)
    }
}
