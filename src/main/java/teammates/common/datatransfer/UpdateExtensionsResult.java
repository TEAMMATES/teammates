package teammates.common.datatransfer;

import java.time.Instant;

import teammates.storage.entity.User;

/**
 * Represents the result of updating a deadline extension for a user.
 *
 * @param user the user for whom the deadline extension was updated
 * @param oldEndTime the old end time of the deadline extension, or the original deadline if the extension was created
 * @param newEndTime the new end time of the deadline extension, or the original deadline if the extension was deleted
 * @param updateType the type of update performed (created, updated, or deleted)
 */
public record UpdateExtensionsResult(
        User user,
        Instant oldEndTime,
        Instant newEndTime,
        ExtensionUpdateType updateType
) {
}
