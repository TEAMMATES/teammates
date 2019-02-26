package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents details of a student account.
 *  * <br> Contains:
 *  * <br> * The section name, team name, comments.
 *  * <br> * Associated opened, not opened, and published sessions.
 */
public class StudentAccountSearchResult extends CommonAccountSearchResult {
    private String section;
    private String team;
    private String comments;

    private String recordsPageLink;

    private Map<String, String> openSessions = new HashMap<>();
    private Map<String, String> notOpenSessions = new HashMap<>();
    private Map<String, String> publishedSessions = new HashMap<>();

    public void setSection(String section) {
        this.section = section;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setRecordsPageLink(String recordsPageLink) {
        this.recordsPageLink = recordsPageLink;
    }

    public void setOpenSessions(Map<String, String> openSessions) {
        this.openSessions = openSessions;
    }

    public void setNotOpenSessions(Map<String, String> notOpenSessions) {
        this.notOpenSessions = notOpenSessions;
    }

    public void setPublishedSessions(Map<String, String> publishedSessions) {
        this.publishedSessions = publishedSessions;
    }

    public String getSection() {
        return section;
    }

    public String getTeam() {
        return team;
    }

    public String getComments() {
        return comments;
    }

    public String getRecordsPageLink() {
        return recordsPageLink;
    }

    public Map<String, String> getOpenSessions() {
        return openSessions;
    }

    public Map<String, String> getNotOpenSessions() {
        return notOpenSessions;
    }

    public Map<String, String> getPublishedSessions() {
        return publishedSessions;
    }
}
