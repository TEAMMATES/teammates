package teammates.ui.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * The request of creating new account.
 */
public class AccountCreateRequest extends BasicRequest {

    private String instructorName;
    private String instructorInstitute;
    private String instructorCountry;
    private String instructorEmail;
    private String instructorHomePageUrl;
    private String otherComments;

    public String getInstructorName() {
        return this.instructorName;
    }

    public String getInstructorInstitute() {
        return this.instructorInstitute;
    }

    public String getInstructorCountry() {
        return this.instructorCountry;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public String getInstructorHomePageUrl() {
        return this.instructorHomePageUrl;
    }

    public String getOtherComments() {
        return this.otherComments;
    }

    public void setInstructorName(String name) {
        this.instructorName = name;
    }

    public void setInstructorInstitute(String institution) {
        this.instructorInstitute = institution;
    }

    public void setInstructorCountry(String country) {
        this.instructorCountry = country;
    }

    public void setInstructorEmail(String email) {
        this.instructorEmail = email;
    }

    public void setInstructorHomePageUrl(String homePageUrl) {
        this.instructorHomePageUrl = homePageUrl;
    }

    public void setOtherComments(String otherComments) {
        this.otherComments = otherComments;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(this.instructorName != null, "name cannot be null");
        assertTrue(this.instructorInstitute != null, "institute cannot be null");
        assertTrue(this.instructorCountry != null, "country cannot be null");
        assertTrue(this.instructorEmail != null, "email cannot be null");

//        List<String> errors = new ArrayList<>();
        Map<String, String> errors = new HashMap<>();

        String nameError = FieldValidator.getInvalidityInfoForPersonName(this.instructorName.trim());
        if (!nameError.isEmpty()) {
            errors.put("name", nameError);
        }

        String instituteError = FieldValidator.getInvalidityInfoForInstituteName(this.instructorInstitute.trim());
        if (!instituteError.isEmpty()) {
            errors.put("institute", instituteError);
        }

        String countryError = FieldValidator.getInvalidityInfoForCountryName(this.instructorCountry.trim());
        if (!countryError.isEmpty()) {
            errors.put("country", countryError);
        }

        String emailError = FieldValidator.getInvalidityInfoForEmail(this.instructorEmail.trim());
        if (!emailError.isEmpty()) {
            errors.put("email", emailError);
        }

//        assertTrue(errors.isEmpty(), StringHelper.toString(errors));
        assertTrue(nameError.isEmpty() && instituteError.isEmpty()
                && countryError.isEmpty() && emailError.isEmpty(), JsonUtils.toJson(errors));
    }
}
