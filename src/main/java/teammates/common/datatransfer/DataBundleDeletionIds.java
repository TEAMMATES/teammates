package teammates.common.datatransfer;

import java.util.List;
import java.util.UUID;

/**
 * Holds the IDs of entities to be deleted.
 *
 * @param accountIds the IDs of accounts to be deleted
 * @param accountRequestIds the IDs of account requests to be deleted
 * @param courseIds the IDs of courses to be deleted
 * @param notificationIds the IDs of notifications to be deleted
 * @param readNotificationIds the IDs of read notifications to be deleted
 */
public record DataBundleDeletionIds(
        List<UUID> accountIds,
        List<UUID> accountRequestIds,
        List<String> courseIds,
        List<UUID> notificationIds,
        List<UUID> readNotificationIds) {
}
