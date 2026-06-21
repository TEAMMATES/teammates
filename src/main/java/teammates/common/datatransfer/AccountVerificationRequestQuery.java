package teammates.common.datatransfer;

import java.util.UUID;

import jakarta.annotation.Nullable;

/**
 * Query criteria for listing account verification requests.
 */
public record AccountVerificationRequestQuery(
        @Nullable UUID instituteId,
        @Nullable UUID accountId,
        @Nullable AccountVerificationRequestStatus status,
        @Nullable String searchKey,
        @Nullable Integer limit) {
}
