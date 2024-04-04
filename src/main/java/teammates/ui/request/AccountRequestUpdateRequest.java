package teammates.ui.request;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.SanitizationHelper;

/**
 * The create request for an account request update request.
 */
public class AccountRequestUpdateRequest extends BasicRequest {
    private String name;
    private String email;
    private String institute;
    private AccountRequestStatus status;
    private String comments;

    public AccountRequestUpdateRequest(String name, String email, String institute, AccountRequestStatus status,
                                       String comments) {
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.institute = SanitizationHelper.sanitizeName(institute);
        this.status = status;
        this.comments = SanitizationHelper.sanitizeTextField(comments);
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(name != null, "name cannot be null");
        assertTrue(email != null, "email cannot be null");
        assertTrue(institute != null, "institute cannot be null");
        assertTrue(status != null, "status cannot be null");
        assertTrue(comments != null, "comments cannot be null");
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getInstitute() {
        return this.institute;
    }

    public AccountRequestStatus getStatus() {
        return this.status;
    }

    public String getComments() {
        return this.comments;
    }
}
