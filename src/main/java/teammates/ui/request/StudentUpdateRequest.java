package teammates.ui.request;

import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;

/**
 * The create request for an student update request.
 */
public class StudentUpdateRequest extends BasicRequest {
    private String name;
    private String email;
    private String team;
    private String section;
    private String comments;
    private Boolean isSessionSummarySendEmail;

    public StudentUpdateRequest(String name, String email, String team, String section, String comments,
                                Boolean isSessionSummarySendEmail) {
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.team = SanitizationHelper.sanitizeName(team);
        this.section = SanitizationHelper.sanitizeName(section);
        this.comments = SanitizationHelper.sanitizeTextField(comments);
        this.isSessionSummarySendEmail = isSessionSummarySendEmail;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(name != null, "name cannot be null");
        assertTrue(email != null, "email cannot be null");
        assertTrue(team != null, "team cannot be null");
        assertTrue(section != null, "section cannot be null");
        assertTrue(comments != null, "comments cannot be null");
        assertTrue(isSessionSummarySendEmail != null, "session summary boolean cannot be null");
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getTeam() {
        return this.team;
    }

    public String getSection() {
        return this.section.isEmpty() ? Const.DEFAULT_SECTION : this.section;
    }

    public String getComments() {
        return this.comments;
    }

    public Boolean getIsSessionSummarySendEmail() {
        return this.isSessionSummarySendEmail;
    }
}
