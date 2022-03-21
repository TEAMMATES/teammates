package teammates.ui.request;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;

/**
 * The basic request of modifying a notification.
 */
public class NotificationBasicRequest extends BasicRequest {
    private Long startTimestamp;
    private Long endTimestamp;
    private NotificationType notificationType;
    private NotificationTargetUser targetUser;
    private String title;
    private String message;

    public Long getStartTimestamp() {
        return this.startTimestamp;
    }

    public Long getEndTimestamp() {
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

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(startTimestamp > 0L, "Start timestamp should be more than zero");
        assertTrue(endTimestamp > 0L, "End timestamp should be more than zero");
        assertTrue(notificationType != null, "Notification type cannot be null");
        assertTrue(targetUser != null, "Notification target user cannot be null");
        assertTrue(title != null, "Notification title cannot be null");
        assertTrue(message != null, "Notification message cannot be null");
    }
}
