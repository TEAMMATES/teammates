package teammates.ui.request;

/**
 * The request of marking a notification as read in account.
 */
public class MarkNotificationAsReadRequest extends BasicRequest {
    private String notificationId;
    private long endTimestamp;

    public MarkNotificationAsReadRequest(String notificationId, Long endTimestamp) {
        this.notificationId = notificationId;
        this.endTimestamp = endTimestamp;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(notificationId != null, "Notification id should not be null.");
        assertTrue(endTimestamp > 0L, "End timestamp should be more than zero");
    }
}
