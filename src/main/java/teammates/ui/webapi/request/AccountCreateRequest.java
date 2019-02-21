package teammates.ui.webapi.request;

import teammates.common.util.FieldValidator;

/**
 * The request of creating new account.
 */
public class AccountCreateRequest extends BasicRequest {

    private String instructorEmail;
    private String instructorName;
    private String instructorInstitution;

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public String getInstructorName() {
        return this.instructorName;
    }

    public String getInstructorInstitution() {
        return this.instructorInstitution;
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

    @Override
    public void validate() {
        FieldValidator validator = new FieldValidator();
        assertTrue(validator.getInvalidityInfoForPersonName(this.instructorName.trim()).isEmpty(), "validate name");
        assertTrue(validator.getInvalidityInfoForInstituteName(this.instructorInstitution.trim()).isEmpty(),
                "validate institute");
        assertTrue(validator.getInvalidityInfoForEmail(this.instructorEmail.trim()).isEmpty(), "validate email");
    }
}
