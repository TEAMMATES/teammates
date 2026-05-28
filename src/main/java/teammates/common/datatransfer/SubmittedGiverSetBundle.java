package teammates.common.datatransfer;

import java.util.Set;
import java.util.UUID;

/**
 * Represents submitted givers partitioned by giver type.
 *
 * @param studentGiverIds    set of student giver ids
 * @param instructorGiverIds set of instructor giver ids
 * @param teamGiverIds       set of team giver ids
 */
public record SubmittedGiverSetBundle(
        Set<UUID> studentGiverIds,
        Set<UUID> instructorGiverIds,
        Set<UUID> teamGiverIds) {
}
