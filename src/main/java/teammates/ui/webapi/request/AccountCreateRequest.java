package teammates.ui.webapi.request;

import teammates.common.util.FieldValidator;

/**
 * The basic request of creating new account.
 */
public class AccountCreateRequest extends BasicRequest {

    private String instructorName;
    private String instructorInstitution;

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

    @Override
    public void validate() {
        this.instructorName = this.instructorName.trim();
        this.instructorInstitution = this.instructorInstitution.trim();

        FieldValidator validator = new FieldValidator();
        assertTrue(validator.getInvalidityInfoForPersonName(this.instructorName).isEmpty(), "validate name");
        assertTrue(validator.getInvalidityInfoForInstituteName(this.instructorInstitution).isEmpty(),
                "validate institute");
    }
}
