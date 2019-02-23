package teammates.ui.webapi.request;

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

    public StudentUpdateRequest(String name, String email, String team, String section, String comments) {
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.team = SanitizationHelper.sanitizeName(team);
        this.section = SanitizationHelper.sanitizeName(section);
        this.comments = SanitizationHelper.sanitizeTextField(comments);
    }

    @Override
    public void validate() {
        assertTrue(name != null, "name cannot be null");
        assertTrue(email != null, "email cannot be null");
        assertTrue(team != null, "team cannot be null");
        assertTrue(section != null, "section cannot be null");
        assertTrue(comments != null, "comments cannot be null");
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
        return this.section;
    }

    public String getComments() {
        return this.comments;
    }
}
