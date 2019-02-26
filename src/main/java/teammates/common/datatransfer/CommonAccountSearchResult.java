package teammates.common.datatransfer;

/**
 * Represents common details of an account.
 *  * <br> Contains:
 *  * <br> * account name, email, google id, course name, institution.
 *  * <br> * link for join course, home page and manage account.
 */
public class CommonAccountSearchResult {
    protected String name;
    protected String email;
    protected String googleId;
    protected String courseId;
    protected String courseName;
    protected String institute;

    protected String courseJoinLink;
    protected String homePageLink;
    protected String manageAccountLink;
    protected boolean showLinks;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseJoinLink(String courseJoinLink) {
        this.courseJoinLink = courseJoinLink;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setHomePageLink(String homePageLink) {
        this.homePageLink = homePageLink;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public void setManageAccountLink(String manageAccountLink) {
        this.manageAccountLink = manageAccountLink;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getInstitute() {
        return institute;
    }

    public String getCourseJoinLink() {
        return courseJoinLink;
    }

    public String getHomePageLink() {
        return homePageLink;
    }

    public String getManageAccountLink() {
        return manageAccountLink;
    }
}
