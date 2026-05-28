package teammates.common.datatransfer;

import java.util.Set;
import java.util.UUID;

/**
 * Represents submitted givers partitioned by giver type.
 *
 * @param studentGiverIds       set of student giver ids
 * @param instructorGiverIds    set of instructor giver ids
 * @param teamGiverIds          set of team giver ids
 * @param studentNonGiverIds    set of students with at least one answerable
 *                              question but no submission
 * @param instructorNonGiverIds set of instructors with at least one answerable
 *                              question but no submission
 * @param teamNonGiverIds       set of teams with at least one answerable
 *                              question but no submission
 */
public record SubmittedGiverSetBundle(
        Set<UUID> studentGiverIds,
        Set<UUID> instructorGiverIds,
        Set<UUID> teamGiverIds,
        Set<UUID> studentNonGiverIds,
        Set<UUID> instructorNonGiverIds,
        Set<UUID> teamNonGiverIds) {

    public SubmittedGiverSetBundle {
        studentGiverIds = Set.copyOf(studentGiverIds);
        instructorGiverIds = Set.copyOf(instructorGiverIds);
        teamGiverIds = Set.copyOf(teamGiverIds);
        studentNonGiverIds = Set.copyOf(studentNonGiverIds);
        instructorNonGiverIds = Set.copyOf(instructorNonGiverIds);
        teamNonGiverIds = Set.copyOf(teamNonGiverIds);
    }
}
