package teammates.ui.output;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;

/**
 * The API output format to represent if there are responses.
 */
public class HasResponsesData extends ApiOutput {
    private final boolean hasResponses; // Used for single entry hasResponses check.
    @Nullable
    private final Map<String, Boolean> hasResponsesBySession; // Used for multi-session hasResponses check.

    /**
     * Constructor for check for presence of responses.
     *
     * @param hasResponses True if has response.
     */
    public HasResponsesData(boolean hasResponses) {
        this.hasResponsesBySession = new HashMap<>(); // unused
        this.hasResponses = hasResponses;
    }

    /**
     * Constructor for multi-session check for presence of responses.
     *
     * @param hasResponsesBySession Map of session name and whether each has response.
     */
    public HasResponsesData(Map<String, Boolean> hasResponsesBySession) {
        this.hasResponsesBySession = hasResponsesBySession;
        this.hasResponses = false; // unused
    }

    /**
     * Return true if has no response.
     */
    public boolean getHasResponses() {
        return hasResponses;
    }

    /**
     * Return a map of session name to whether it has responses.
     */
    public Map<String, Boolean> getHasResponsesBySessions() {
        return hasResponsesBySession;
    }
}
