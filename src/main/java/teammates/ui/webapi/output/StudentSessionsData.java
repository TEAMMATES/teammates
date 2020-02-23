package teammates.ui.webapi.output;

import java.util.HashMap;
import java.util.Map;

/**
 * Output format for a student's session data.
 */
public class StudentSessionsData extends ApiOutput {
    private final Map<String, String> openSessions = new HashMap<>();
    private final Map<String, String> closedSessions = new HashMap<>();
    private final Map<String, String> publishedSessions = new HashMap<>();

    public void addOpenSession(String sessionName, String sessionUrl) {
        openSessions.put(sessionName, sessionUrl);
    }

    public void addClosedSession(String sessionName, String sessionUrl) {
        closedSessions.put(sessionName, sessionUrl);
    }

    public void addPublishedSession(String sessionName, String sessionUrl) {
        publishedSessions.put(sessionName, sessionUrl);
    }
}
