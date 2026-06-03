package teammates.common.datatransfer;

import java.util.Set;
import java.util.UUID;

/**
 * Represents submitted and non-submitted givers by user type.
 *
 * @param studentGiverIds       set of student giver ids; includes students in a responding team
 * @param instructorGiverIds    set of instructor giver ids
 * @param studentNonGiverIds    set of students with at least one answerable
 *                              question but no submission
 * @param instructorNonGiverIds set of instructors with at least one answerable
 *                              question but no submission
 */
public record SubmittedGiverSetBundle(
        Set<UUID> studentGiverIds,
        Set<UUID> instructorGiverIds,
        Set<UUID> studentNonGiverIds,
        Set<UUID> instructorNonGiverIds) {

    public SubmittedGiverSetBundle {
        studentGiverIds = Set.copyOf(studentGiverIds);
        instructorGiverIds = Set.copyOf(instructorGiverIds);
        studentNonGiverIds = Set.copyOf(studentNonGiverIds);
        instructorNonGiverIds = Set.copyOf(instructorNonGiverIds);
    }
}
