package teammates.common.datatransfer;

import java.util.UUID;

import jakarta.annotation.Nullable;

/**
 * Query criteria for listing account verification requests.
 *
 * @param instituteId the ID of the institute to filter by, or {@code null} to not filter by institute
 * @param accountId the ID of the account to filter by, or {@code null} to not filter by account
 * @param status the status to filter by, or {@code null} to not filter by status
 * @param searchKey the search key to filter by, or {@code null} to not filter by search key
 * @param limit the maximum number of results to return, or {@code null} to not limit the number of results
 */
public record AccountVerificationRequestQuery(
        @Nullable UUID instituteId,
        @Nullable UUID accountId,
        @Nullable AccountVerificationRequestStatus status,
        @Nullable String searchKey,
        @Nullable Integer limit) {
}
