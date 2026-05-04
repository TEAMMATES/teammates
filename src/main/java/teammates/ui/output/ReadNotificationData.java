package teammates.ui.output;

import java.util.UUID;

import teammates.storage.entity.ReadNotification;

/**
 * Output format of a read notification.
 */
public class ReadNotificationData extends ApiOutput {
    private final UUID readNotificationId;
    private final UUID accountId;
    private final UUID notificationId;

    public ReadNotificationData(ReadNotification readNotification) {
        this.readNotificationId = readNotification.getId();
        this.accountId = readNotification.getAccountId();
        this.notificationId = readNotification.getNotificationId();
    }

    public UUID getReadNotificationId() {
        return readNotificationId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getNotificationId() {
        return notificationId;
    }
}
