package teammates.ui.request;

/**
 * The request for creating a notification.
 */
public class CreateNotificationRequest extends BasicRequest {
    private Long startTimestamp;
    private Long endTimestamp;
    private String notificationType;
    private String targetUser;
    private String title;
    private String message;

    public Long getStartTimestamp() {
        return this.startTimestamp;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    public String getNotificationType() {
        return this.notificationType;
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

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(startTimestamp > 0L, "Start timestamp should be more than zero");
        assertTrue(endTimestamp > 0L, "End timestamp should be more than zero");
        assertTrue(notificationType != null, "Notification type cannot be null");
        assertTrue(targetUser != null, "User group to show notification cannot be null");
        assertTrue(title != null, "Notification title cannot be null");
        assertTrue(message != null, "Notification message cannot be null");

    }
}
