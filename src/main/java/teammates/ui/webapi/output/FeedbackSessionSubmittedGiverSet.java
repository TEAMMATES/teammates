package teammates.ui.webapi.output;

import java.util.Set;

/**
 * The API output format of all givers who submitted a feedback session.
 */
public class FeedbackSessionSubmittedGiverSet extends ApiOutput {
    private final Set<String> giverIdentifiers;

    public FeedbackSessionSubmittedGiverSet(Set<String> giverIdentifiers) {
        this.giverIdentifiers = giverIdentifiers;
    }

    public Set<String> getGiverIdentifiers() {
        return giverIdentifiers;
    }
}
