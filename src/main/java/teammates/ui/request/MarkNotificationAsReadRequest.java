package teammates.ui.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request of marking a notification as read in account.
 */
public class MarkNotificationAsReadRequest extends BasicRequest {
    private String notificationId;
    private List<String> notificationIds;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MarkNotificationAsReadRequest(
            @JsonProperty("notificationId") String notificationId,
            @JsonProperty("notificationIds") List<String> notificationIds) {
        this.notificationId = notificationId;
        this.notificationIds = notificationIds;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    public List<String> getNotificationIds() {
        return this.notificationIds;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(notificationId != null || (notificationIds != null && !notificationIds.isEmpty()),
                "Either notificationId or notificationIds should be provided.");
    }
}
