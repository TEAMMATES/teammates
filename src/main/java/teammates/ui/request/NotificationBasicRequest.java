package teammates.ui.request;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;

/**
 * The basic request for a notification.
 */
public class NotificationBasicRequest extends BasicRequest {
    private long startTimestamp;
    private long endTimestamp;
    private NotificationType notificationType;
    private NotificationTargetUser targetUser;
    private String title;
    private String message;

    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    public long getEndTimestamp() {
        return this.endTimestamp;
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

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void setTargetUser(NotificationTargetUser targetUser) {
        this.targetUser = targetUser;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(startTimestamp > 0L, "Start timestamp should be greater than zero");
        assertTrue(endTimestamp > 0L, "End timestamp should be greater than zero");
        assertTrue(notificationType != null, "Notification type cannot be null");
        assertTrue(targetUser != null, "Notification target user cannot be null");
        assertTrue(title != null, "Notification title cannot be null");
        assertTrue(message != null, "Notification message cannot be null");
    }
}
