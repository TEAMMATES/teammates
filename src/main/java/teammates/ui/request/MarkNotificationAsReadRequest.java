package teammates.ui.request;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The request of marking a notification as read in account.
 */
public class MarkNotificationAsReadRequest extends BasicRequest {
    private String notificationId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MarkNotificationAsReadRequest(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(notificationId != null, "Notification id should not be null.");
    }
}
