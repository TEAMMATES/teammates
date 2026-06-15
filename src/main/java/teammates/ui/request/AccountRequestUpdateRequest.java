package teammates.ui.request;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.SanitizationHelper;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The create request for an account request update request.
 */
public class AccountRequestUpdateRequest extends BasicRequest {
    private String name;
    private String email;
    private String institute;
    private String country;
    private AccountRequestStatus status;

    @Nullable
    private String comments;

    private AccountRequestUpdateRequest() {
        // for Jackson deserialization
    }

    public AccountRequestUpdateRequest(String name, String email, String institute, String country,
                                       AccountRequestStatus status, String comments) {
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
        validateTrue(status == AccountRequestStatus.APPROVED
                || status == AccountRequestStatus.REJECTED
                || status == AccountRequestStatus.PENDING,
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

    public AccountRequestStatus getStatus() {
        return this.status;
    }

    public String getComments() {
        return this.comments;
    }
}
