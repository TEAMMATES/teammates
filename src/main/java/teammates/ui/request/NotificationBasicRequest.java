package teammates.ui.request;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.common.datatransfer.NotificationStyle;
import teammates.ui.exception.InvalidHttpRequestBodyException;
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
        validateTrue(startTimestamp > 0L, "Start timestamp should be greater than zero");
        validateTrue(endTimestamp > 0L, "End timestamp should be greater than zero");
        validateTrue(style != null, "Notification style cannot be null");
        validateTrue(targetUser != null, "Notification target user cannot be null");
        validateTrue(title != null, "Notification title cannot be null");
        validateTrue(!title.trim().isEmpty(), "Notification title cannot be empty");
        validateTrue(message != null, "Notification message cannot be null");
    }
}
