package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.common.util.JsonUtils;

/**
 * Representation of notifications that a user has read, containing a map of notification ID to end timestamp.
 */
public class ReadNotifications {
    private final Map<String, Long> idToEndTimestampMap;

    public ReadNotifications() {
        this.idToEndTimestampMap = new LinkedHashMap<>();
    }

    public ReadNotifications(Map<String, Long> idToEndTimestampMap) {
        this.idToEndTimestampMap = idToEndTimestampMap;
    }

    public Map<String, Long> getIdToEndTimestampMap() {
        return idToEndTimestampMap;
    }

    /**
     * Returns a JSON string representation of the notification read status.
     */
    public String getJsonString() {
        return JsonUtils.toJson(this, this.getClass());
    }

    /**
     * Returns a deep copy of the notification read status.
     */
    public ReadNotifications getDeepCopy() {
        String serializedReadNotifications = getJsonString();
        return JsonUtils.fromJson(serializedReadNotifications, this.getClass());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        // Json string contains all attributes of a `ReadNotifications` object,
        // so it is sufficient to use it to compare two `ReadNotifications` objects.
        ReadNotifications other = (ReadNotifications) obj;
        return this.getJsonString().equals(other.getJsonString());
    }

    @Override
    public int hashCode() {
        return this.getJsonString().hashCode();
    }
}
