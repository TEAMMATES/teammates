package teammates.ui.request;

/**
 * The request of creating a read notification in account.
 */
public class ReadNotificationCreateRequest extends BasicRequest {
    private String notificationId;
    private Long endTimestamp;

    public ReadNotificationCreateRequest(String notificationId, Long endTimestamp) {
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
        assertTrue(notificationId != null, "Notification id should not be invalid.");
        assertTrue(endTimestamp != null, "End timestamp cannot be null");
        assertTrue(endTimestamp > 0L, "End timestamp should be more than zero");
    }
}
