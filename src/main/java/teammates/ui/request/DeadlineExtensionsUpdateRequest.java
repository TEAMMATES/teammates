package teammates.ui.request;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The update request for deadline extensions of a feedback session.
 */
public class DeadlineExtensionsUpdateRequest extends BasicRequest {

    private Map<UUID, Long> userDeadlines;

    /**
     * Gets the deadlines for users.
     */
    public Map<UUID, Instant> getUserDeadlines() {
        return userDeadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Instant.ofEpochMilli(entry.getValue())));
    }

    public void setUserDeadlines(Map<UUID, Long> userDeadlines) {
        this.userDeadlines = userDeadlines;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(userDeadlines != null, "deadlines for users cannot be null");

        for (Map.Entry<UUID, Long> entry : userDeadlines.entrySet()) {
            UUID userId = entry.getKey();
            Long deadlineMillis = entry.getValue();

            validateTrue(userId != null, "user ID cannot be null");
            validateTrue(deadlineMillis != null && deadlineMillis > 0,
                    "user deadline must be a positive epoch milli value");
        }
    }
}
