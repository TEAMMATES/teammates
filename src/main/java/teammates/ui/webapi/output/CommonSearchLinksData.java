package teammates.ui.webapi.output;

/**
 * Contains the common links for instructors and students.
 */
public class CommonSearchLinksData {
    protected boolean showLinks;
    protected String email;
    protected String manageAccountLink;
    protected String homePageLink;
    protected String courseJoinLink;

    public CommonSearchLinksData() {
        this.email = null;
        this.manageAccountLink = null;
        this.homePageLink = null;
        this.courseJoinLink = null;
        this.showLinks = false;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setManageAccountLink(String manageAccountLink) {
        this.manageAccountLink = manageAccountLink;
    }

    public void setHomePageLink(String homePageLink) {
        this.homePageLink = homePageLink;
    }

    public void setCourseJoinLink(String courseJoinLink) {
        this.courseJoinLink = courseJoinLink;
    }

    public String getEmail() {
        return email;
    }

    public String getManageAccountLink() {
        return manageAccountLink;
    }

    public String getHomePageLink() {
        return homePageLink;
    }

    public String getCourseJoinLink() {
        return courseJoinLink;
    }
}
