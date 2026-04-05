package teammates.ui.request;

/**
 * The request of marking a notification as read in account.
 */
public class MarkNotificationAsReadRequest extends BasicRequest {
    private String notificationId;

    public MarkNotificationAsReadRequest(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(notificationId != null, "Notification id should not be null.");
    }
}
