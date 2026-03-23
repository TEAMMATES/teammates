package teammates.ui.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;

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

    /**
     * For Gson deserialization.
     */
    @SuppressWarnings("unused")
    private AccountRequestUpdateRequest() {
    }

    public AccountRequestUpdateRequest(String name, String email, String institute, String country,
            AccountRequestStatus status, String comments) {
        this.name = name;
        this.email = email;
        this.institute = institute;
        this.country = country;
        this.status = status;
        this.comments = comments;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(name != null, "name cannot be null");
        assertTrue(email != null, "email cannot be null");
        assertTrue(institute != null, "institute cannot be null");
        assertTrue(country != null, "country cannot be null");
        assertTrue(status != null, "status cannot be null");
        assertTrue(status == AccountRequestStatus.APPROVED
                || status == AccountRequestStatus.REJECTED
                || status == AccountRequestStatus.PENDING
                || status == AccountRequestStatus.REGISTERED,
                "status must be one of the following: APPROVED, REJECTED, PENDING, REGISTERED");

        List<String> errors = new ArrayList<>();

        String nameError = FieldValidator.getInvalidityInfoForPersonName(name.trim());
        if (!nameError.isEmpty()) {
            errors.add(nameError);
        }

        String emailError = FieldValidator.getInvalidityInfoForEmail(email.trim());
        if (!emailError.isEmpty()) {
            errors.add(emailError);
        }

        String instituteError = FieldValidator.getInvalidityInfoForInstituteName(institute.trim());
        if (!instituteError.isEmpty()) {
            errors.add(instituteError);
        }

        String countryError = FieldValidator.getInvalidityInfoForCountryName(country.trim());
        if (!countryError.isEmpty()) {
            errors.add(countryError);
        }

        assertTrue(errors.isEmpty(), StringHelper.toString(errors));
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
