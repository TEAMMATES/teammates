package teammates.ui.webapi.request;

import teammates.common.util.FieldValidator;

/**
 * The request of creating new account.
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
        this.instructorName = name.trim();
    }

    public void setInstructorInstitution(String institution) {
        this.instructorInstitution = institution.trim();
    }

    @Override
    public void validate() {
        FieldValidator validator = new FieldValidator();
        assertTrue(validator.getInvalidityInfoForPersonName(this.instructorName).isEmpty(), "validate name");
        assertTrue(validator.getInvalidityInfoForInstituteName(this.instructorInstitution).isEmpty(),
                "validate institute");
    }
}
