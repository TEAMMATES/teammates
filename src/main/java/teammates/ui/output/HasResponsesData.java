package teammates.ui.output;

/**
 * The API output format to represent if there are responses.
 */
public class HasResponsesData extends ApiOutput {
    private final boolean hasResponses;

    public HasResponsesData(boolean hasResponses) {
        this.hasResponses = hasResponses;
    }

    /**
     * Returns true if there are responses, false otherwise.
     */
    public boolean hasResponses() {
        return hasResponses;
    }
}
