package teammates.common.datatransfer;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents the result of updating a deadline extension for a user.
 *
 * @param userId the user for whom the deadline extension was updated
 * @param oldEndTime the old end time of the deadline extension, or the original deadline if the extension was created
 * @param newEndTime the new end time of the deadline extension, or the original deadline if the extension was deleted
 * @param updateType the type of update performed
 */
public record UpdateExtensionsResult(
        UUID userId,
        Instant oldEndTime,
        Instant newEndTime,
        ExtensionUpdateType updateType
) {
}
