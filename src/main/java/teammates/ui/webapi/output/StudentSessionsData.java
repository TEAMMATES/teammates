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

    /**
     * Adds a new open session.
     * @param sessionName Name of session
     * @param sessionUrl Submission URL
     */
    public void addOpenSession(String sessionName, String sessionUrl) {
        openSessions.put(sessionName, sessionUrl);
    }

    /**
     * Adds a new closed session.
     * @param sessionName Name of session
     * @param sessionUrl Submission URL
     */
    public void addClosedSession(String sessionName, String sessionUrl) {
        closedSessions.put(sessionName, sessionUrl);
    }

    /**
     * Adds a new published session.
     * @param sessionName Name of session
     * @param sessionUrl Submission URL
     */
    public void addPublishedSession(String sessionName, String sessionUrl) {
        publishedSessions.put(sessionName, sessionUrl);
    }
}
