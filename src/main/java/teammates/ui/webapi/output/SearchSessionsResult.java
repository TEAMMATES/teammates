package teammates.ui.webapi.output;

import java.util.Map;

/**
 * Contains Sessions search results.
 */
public class SearchSessionsResult extends ApiOutput {
    private final Map<String, SearchSessionData> sessions;

    public SearchSessionsResult(Map<String, SearchSessionData> sessions) {
        this.sessions = sessions;
    }

    public Map<String, SearchSessionData> getSessions() {
        return sessions;
    }
}
