package teammates.ui.request;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;

/**
 * The basic request for a notification.
 */
public class NotificationBasicRequest extends BasicRequest {
    private long startTimestamp;
    private long endTimestamp;
    private NotificationStyle style;
    private NotificationTargetUser targetUser;
    private String title;
    private String message;

    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    public long getEndTimestamp() {
        return this.endTimestamp;
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

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setStyle(NotificationStyle style) {
        this.style = style;
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
        assertTrue(style != null, "Notification style cannot be null");
        assertTrue(targetUser != null, "Notification target user cannot be null");
        assertTrue(title != null, "Notification title cannot be null");
        assertTrue(!title.trim().isEmpty(), "Notification title cannot be empty");
        assertTrue(message != null, "Notification message cannot be null");
    }
}
