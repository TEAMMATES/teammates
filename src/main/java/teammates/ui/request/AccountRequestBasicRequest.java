package teammates.ui.request;

/**
 * The basic request body for creating/updating an account request.
 */
public class AccountRequestBasicRequest extends BasicRequest {

    private String instructorName;
    private String instructorEmail;
    private String instructorInstitute;

    public String getInstructorName() {
        return this.instructorName;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public String getInstructorInstitute() {
        return this.instructorInstitute;
    }

    public void setInstructorName(String name) {
        this.instructorName = name;
    }

    public void setInstructorEmail(String email) {
        this.instructorEmail = email;
    }

    public void setInstructorInstitute(String institute) {
        this.instructorInstitute = institute;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(this.instructorName != null, "name cannot be null");
        assertTrue(this.instructorEmail != null, "email cannot be null");
        assertTrue(this.instructorInstitute != null, "institute cannot be null");
    }
}
