package teammates.ui.webapi.output;

import java.util.Map;

/**
 * Output format for session search result.
 */
public class SearchSessionsData extends ApiOutput {
    private final Map<String, StudentSessionsData> sessions;

    public SearchSessionsData(Map<String, StudentSessionsData> sessions) {
        this.sessions = sessions;
    }

    public Map<String, StudentSessionsData> getSessions() {
        return sessions;
    }
}
