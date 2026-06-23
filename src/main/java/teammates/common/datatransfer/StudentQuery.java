package teammates.common.datatransfer;

import java.util.List;

import jakarta.annotation.Nullable;

/**
 * Query criteria for listing or searching students.
 *
 * @param courseIds the IDs of the courses to filter by, or {@code null} to search across all courses
 * @param searchKey the search key to filter by, or {@code null} to list students without search filtering
 * @param limit the maximum number of results to return, or {@code null} to not limit the number of results
 */
public record StudentQuery(
        @Nullable List<String> courseIds,
        @Nullable String searchKey,
        @Nullable Integer limit) {
}
