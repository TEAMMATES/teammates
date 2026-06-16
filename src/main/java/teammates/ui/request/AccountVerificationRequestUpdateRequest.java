package teammates.ui.request;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.SanitizationHelper;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The create request for an account verification request update request.
 */
public class AccountVerificationRequestUpdateRequest extends BasicRequest {
    private String name;
    private String email;
    private String institute;
    private String country;
    private AccountVerificationRequestStatus status;

    @Nullable
    private String comments;

    private AccountVerificationRequestUpdateRequest() {
        // for Jackson deserialization
    }

    public AccountVerificationRequestUpdateRequest(String name, String email, String institute, String country,
                                       AccountVerificationRequestStatus status, String comments) {
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.institute = SanitizationHelper.sanitizeName(institute);
        this.country = country;
        this.status = status;
        if (comments != null) {
            this.comments = SanitizationHelper.sanitizeTextField(comments);
        }
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(name != null, "name cannot be null");
        validateTrue(email != null, "email cannot be null");
        validateTrue(institute != null, "institute cannot be null");
        validateTrue(country != null, "country cannot be null");
        validateTrue(status != null, "status cannot be null");
        validateTrue(status == AccountVerificationRequestStatus.APPROVED
                || status == AccountVerificationRequestStatus.REJECTED
                || status == AccountVerificationRequestStatus.PENDING,
                "status must be one of the following: APPROVED, REJECTED, PENDING");
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

    public String getCountry() {
        return this.country;
    }

    public AccountVerificationRequestStatus getStatus() {
        return this.status;
    }

    public String getComments() {
        return this.comments;
    }
}
