package teammates.common.datatransfer;

import jakarta.annotation.Nullable;

/**
 * Query criteria for listing instructors.
 *
 * @param courseId the ID of the course to filter by, or {@code null} to not filter by course
 * @param searchKey the search key to filter by, or {@code null} to not filter by search key
 * @param limit the maximum number of results to return, or {@code null} to not limit the number of results
 */
public record InstructorQuery(
        @Nullable String courseId,
        @Nullable String searchKey,
        @Nullable Integer limit) {
}
