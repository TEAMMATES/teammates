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

    private StudentUpdateRequest() {
        // for Jackson deserialization
    }

    public StudentUpdateRequest(String name, String email, String team, String section, String comments,
                                Boolean isSessionSummarySendEmail) {
        this.name = name;
        this.email = email;
        this.team = team;
        this.section = section;
        this.comments = comments;
        this.isSessionSummarySendEmail = isSessionSummarySendEmail;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(getName() != null, "name cannot be null");
        validateTrue(getEmail() != null, "email cannot be null");
        validateTrue(this.team != null, "team cannot be null");
        validateFalse(getTeam().trim().isEmpty(), "team cannot be blank");
        validateTrue(this.section != null, "section cannot be null");
        validateFalse(getSection().trim().isEmpty(), "section cannot be blank");
        validateTrue(getComments() != null, "comments cannot be null");
        validateTrue(isSessionSummarySendEmail != null, "session summary boolean cannot be null");
    }

    public String getName() {
        return SanitizationHelper.sanitizeName(this.name);
    }

    public String getEmail() {
        return SanitizationHelper.sanitizeEmail(this.email);
    }

    public String getTeam() {
        return SanitizationHelper.sanitizeName(this.team);
    }

    public String getSection() {
        return this.section.isEmpty() ? Const.DEFAULT_SECTION : SanitizationHelper.sanitizeName(this.section);
    }

    public String getComments() {
        return SanitizationHelper.sanitizeTextField(this.comments);
    }

    public boolean getIsSessionSummarySendEmail() {
        return this.isSessionSummarySendEmail != null && this.isSessionSummarySendEmail;
    }
}
