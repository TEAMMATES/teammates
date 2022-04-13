package teammates.ui.output;

import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Output format of read notifications.
 */
public class ReadNotificationsData extends ApiOutput {

    private final Map<String, Long> readNotifications;

    public ReadNotificationsData(AccountAttributes accountInfo) {
        this.readNotifications = accountInfo.getReadNotifications()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().toEpochMilli()
                ));
    }

    public Map<String, Long> getReadNotifications() {
        return this.readNotifications;
    }

}
