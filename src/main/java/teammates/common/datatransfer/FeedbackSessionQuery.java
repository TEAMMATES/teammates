package teammates.common.datatransfer;

import java.util.List;

import jakarta.annotation.Nullable;

/**
 * Query criteria for listing or searching feedback sessions.
 *
 * @param courseIds the IDs of the courses to filter by, or {@code null} to search across all courses
 * @param isInRecycleBin whether to return sessions in the recycle bin, or {@code null} to use the default
 */
public record FeedbackSessionQuery(
        @Nullable List<String> courseIds,
        @Nullable Boolean isInRecycleBin) {
}
