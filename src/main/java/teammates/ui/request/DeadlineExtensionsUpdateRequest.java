package teammates.ui.request;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        assertTrue(userDeadlines != null, "deadlines for users cannot be null");

        for (Map.Entry<UUID, Long> entry : userDeadlines.entrySet()) {
            UUID userId = entry.getKey();
            Long deadlineMillis = entry.getValue();

            assertTrue(userId != null, "user ID cannot be null");
            assertTrue(deadlineMillis != null && deadlineMillis > 0,
                    "user deadline must be a positive epoch milli value");
        }
    }
}
