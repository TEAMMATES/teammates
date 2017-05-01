package teammates.ui.pagedata;

/**
 * PageData: this is page data for an 'Account Request' of an instructor.
 */

public class RequestPageData extends PageData {
	private String name;
    private String university;
    private String country;
    private String url;
    private String email;
    private String comments;

    public RequestPageData(String name, String university, String country, String url, String email, String comments) {
        this.name = name;
        this.university = university;
        this.country = country;
        this.url = url;
        this.email = email;
        this.comments = comments;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getUniversity() {
    	return university;
    }
    
    public String getCountry() {
    	return country;
    }
    
    public String getUrl() {
    	return url;
    }
    
    public String getEmail() {
    	return email;
    }
    
    public String getComments() {
    	return comments;
    }

    public String getInstructorRequestAccountLink() {
        return getInstructorRequestAccountLink();
    }
}