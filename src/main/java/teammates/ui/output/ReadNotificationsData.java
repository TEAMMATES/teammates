package teammates.ui.output;

import java.util.List;

/**
 * Output format of read notifications.
 */
public class ReadNotificationsData extends ApiOutput {

    private final List<String> readNotifications;

    public ReadNotificationsData(List<String> notificationIds) {
        this.readNotifications = notificationIds;
    }

    public List<String> getReadNotifications() {
        return this.readNotifications;
    }

}
