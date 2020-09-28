package teammates.ui.output;

import java.util.List;
import java.util.Map;

/**
 * The API output format for OngoingSessions.
 */
public class OngoingSessionsData extends ApiOutput {

    private int totalOngoingSessions;
    private int totalOpenSessions;
    private int totalClosedSessions;
    private int totalAwaitingSessions;
    private long totalInstitutes;
    private Map<String, List<OngoingSession>> sessions;

    public int getTotalOngoingSessions() {
        return totalOngoingSessions;
    }

    public int getTotalOpenSessions() {
        return totalOpenSessions;
    }

    public int getTotalClosedSessions() {
        return totalClosedSessions;
    }

    public int getTotalAwaitingSessions() {
        return totalAwaitingSessions;
    }

    public long getTotalInstitutes() {
        return totalInstitutes;
    }

    public Map<String, List<OngoingSession>> getSessions() {
        return sessions;
    }

    public void setTotalOngoingSessions(int totalOngoingSessions) {
        this.totalOngoingSessions = totalOngoingSessions;
    }

    public void setTotalOpenSessions(int totalOpenSessions) {
        this.totalOpenSessions = totalOpenSessions;
    }

    public void setTotalClosedSessions(int totalClosedSessions) {
        this.totalClosedSessions = totalClosedSessions;
    }

    public void setTotalAwaitingSessions(int totalAwaitingSessions) {
        this.totalAwaitingSessions = totalAwaitingSessions;
    }

    public void setTotalInstitutes(long totalInstitutes) {
        this.totalInstitutes = totalInstitutes;
    }

    public void setSessions(Map<String, List<OngoingSession>> sessions) {
        this.sessions = sessions;
    }
}
