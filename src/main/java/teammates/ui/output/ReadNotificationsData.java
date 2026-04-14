package teammates.ui.output;

import java.util.List;
import java.util.UUID;

/**
 * Output format of read notifications.
 */
public class ReadNotificationsData extends ApiOutput {

    private final List<UUID> readNotifications;

    public ReadNotificationsData(List<UUID> notificationIds) {
        this.readNotifications = notificationIds;
    }

    public List<UUID> getReadNotifications() {
        return this.readNotifications;
    }

}
