package teammates.ui.request;

import java.util.List;

/**
 * The request for marking multiple notifications as read in an account.
 */
public class MarkNotificationsAsReadRequest extends BasicRequest {

    private List<NotificationReadStatus> notifications;

    public MarkNotificationsAsReadRequest() {
    }

    public MarkNotificationsAsReadRequest(List<NotificationReadStatus> notifications) {
        this.notifications = notifications;
    }

    public List<NotificationReadStatus> getNotifications() {
        return notifications;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(notifications != null && !notifications.isEmpty(), "Notifications list should not be null or empty.");
        for (NotificationReadStatus notification : notifications) {
            assertTrue(notification != null, "Notification status should not be null.");
            assertTrue(notification.getNotificationId() != null, "Notification id should not be null.");
            assertTrue(notification.getEndTimestamp() > 0L, "End timestamp should be more than zero");
        }
    }

    public static class NotificationReadStatus {
        private String notificationId;
        private long endTimestamp;

        public NotificationReadStatus() {
        }

        public NotificationReadStatus(String notificationId, long endTimestamp) {
            this.notificationId = notificationId;
            this.endTimestamp = endTimestamp;
        }

        public String getNotificationId() {
            return notificationId;
        }

        public long getEndTimestamp() {
            return endTimestamp;
        }
    }
}
