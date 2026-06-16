package teammates.ui.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;

import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The request of creating new account.
 */
public class AccountCreateRequest extends BasicRequest {

    private String instructorEmail;
    private String instructorName;
    private String instructorInstitution;
    private String instructorCountry;
    @Nullable
    private String instructorComments;

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public String getInstructorName() {
        return this.instructorName;
    }

    public String getInstructorInstitution() {
        return this.instructorInstitution;
    }

    public String getInstructorCountry() {
        return this.instructorCountry;
    }

    public String getInstructorComments() {
        return this.instructorComments;
    }

    public void setInstructorName(String name) {
        this.instructorName = name;
    }

    public void setInstructorInstitution(String institution) {
        this.instructorInstitution = institution;
    }

    public void setInstructorCountry(String country) {
        this.instructorCountry = country;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public void setInstructorComments(String instructorComments) {
        this.instructorComments = instructorComments;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(this.instructorEmail != null, "email cannot be null");
        validateTrue(this.instructorName != null, "name cannot be null");
        validateTrue(this.instructorInstitution != null, "institute cannot be null");
        validateTrue(this.instructorCountry != null, "country cannot be null");

        List<String> errors = new ArrayList<>();

        String nameError = FieldValidator.getInvalidityInfoForPersonName(this.instructorName.trim());
        if (!nameError.isEmpty()) {
            errors.add(nameError);
        }

        String emailError = FieldValidator.getInvalidityInfoForEmail(this.instructorEmail.trim());
        if (!emailError.isEmpty()) {
            errors.add(emailError);
        }

        String instituteError = FieldValidator.getInvalidityInfoForInstituteName(this.instructorInstitution.trim());
        if (!instituteError.isEmpty()) {
            errors.add(instituteError);
        }

        String countryError = FieldValidator.getInvalidityInfoForCountry(this.instructorCountry.trim());
        if (!countryError.isEmpty()) {
            errors.add(countryError);
        }

        validateTrue(errors.isEmpty(), StringHelper.toString(errors));
    }
}
