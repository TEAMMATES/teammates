package teammates.ui.request;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import java.util.ArrayList;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import java.util.List;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import jakarta.annotation.Nullable;

import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.common.util.FieldValidator;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.common.util.StringHelper;

/**
 * The request of creating new account.
 */
public class AccountCreateRequest extends BasicRequest {

    private String instructorEmail;
    private String instructorName;
    private String instructorInstitution;
    @Nullable
    private String instructorComments;
    @Nullable
    private String captchaResponse;

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public String getInstructorName() {
        return this.instructorName;
    }

    public String getInstructorInstitution() {
        return this.instructorInstitution;
    }

    public String getInstructorComments() {
        return this.instructorComments;
    }

    public String getCaptchaResponse() {
        return this.captchaResponse;
    }

    public void setInstructorName(String name) {
        this.instructorName = name;
    }

    public void setInstructorInstitution(String institution) {
        this.instructorInstitution = institution;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public void setInstructorComments(String instructorComments) {
        this.instructorComments = instructorComments;
    }

    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(this.instructorEmail != null, "email cannot be null");
        validateTrue(this.instructorName != null, "name cannot be null");
        validateTrue(this.instructorInstitution != null, "institute cannot be null");

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

        validateTrue(errors.isEmpty(), StringHelper.toString(errors));
    }
}
