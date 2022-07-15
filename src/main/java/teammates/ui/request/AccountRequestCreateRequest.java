package teammates.ui.request;

/**
 * The request body for creating a new account request.
 */
public class AccountRequestCreateRequest extends AccountRequestBasicRequest {

    private String instructorCountry;
    private String instructorHomePageUrl;
    private String comments;

    public String getInstructorCountry() {
        return this.instructorCountry;
    }

    public String getInstructorHomePageUrl() {
        return this.instructorHomePageUrl;
    }

    public String getComments() {
        return this.comments;
    }

    public void setInstructorCountry(String country) {
        this.instructorCountry = country;
    }

    public void setInstructorHomePageUrl(String homePageUrl) {
        this.instructorHomePageUrl = homePageUrl;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        super.validate();

        assertTrue(this.instructorCountry != null, "country cannot be null");
        assertTrue(this.instructorHomePageUrl != null, "home page url cannot be null");
        assertTrue(this.comments != null, "comments cannot be null");
    }
}
