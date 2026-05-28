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
    private final Set<UUID> studentNonGivers;
    private final Set<UUID> instructorNonGivers;

    public FeedbackSessionSubmittedGiverSet(SubmittedGiverSetBundle submittedGiverSetBundle) {
        this.studentGivers = submittedGiverSetBundle.studentGiverIds();
        this.instructorGivers = submittedGiverSetBundle.instructorGiverIds();
        this.studentNonGivers = submittedGiverSetBundle.studentNonGiverIds();
        this.instructorNonGivers = submittedGiverSetBundle.instructorNonGiverIds();
    }

    public Set<UUID> getStudentGivers() {
        return studentGivers;
    }

    public Set<UUID> getInstructorGivers() {
        return instructorGivers;
    }

    public Set<UUID> getStudentNonGivers() {
        return studentNonGivers;
    }

    public Set<UUID> getInstructorNonGivers() {
        return instructorNonGivers;
    }
}
