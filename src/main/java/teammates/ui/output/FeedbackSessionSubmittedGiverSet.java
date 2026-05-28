package teammates.ui.output;

import java.util.Set;
import java.util.UUID;

import teammates.common.datatransfer.SubmittedGiverSetBundle;

/**
 * The API output format of all givers who submitted a feedback session.
 */
public class FeedbackSessionSubmittedGiverSet extends ApiOutput {
    private final Set<UUID> studentGivers;
    private final Set<UUID> instructorGivers;
    private final Set<UUID> teamGivers;

    public FeedbackSessionSubmittedGiverSet(SubmittedGiverSetBundle submittedGiverSetBundle) {
        this.studentGivers = submittedGiverSetBundle.studentGiverIds();
        this.instructorGivers = submittedGiverSetBundle.instructorGiverIds();
        this.teamGivers = submittedGiverSetBundle.teamGiverIds();
    }

    public Set<UUID> getStudentGivers() {
        return studentGivers;
    }

    public Set<UUID> getInstructorGivers() {
        return instructorGivers;
    }

    public Set<UUID> getTeamGivers() {
        return teamGivers;
    }
}
