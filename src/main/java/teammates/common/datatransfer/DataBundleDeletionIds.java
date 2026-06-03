package teammates.common.datatransfer;

import java.util.List;
import java.util.UUID;

/**
 * Holds the IDs of entities to be deleted.
 */
// CHECKSTYLE.OFF:JavadocVariable each field represents different entity types
public record DataBundleDeletionIds(
        List<UUID> accountIds,
        List<UUID> accountRequestIds,
        List<String> courseIds,
        List<UUID> notificationIds,
        List<UUID> readNotificationIds) {
}
