package teammates.ui.template;

public class AdminSearchStudentLinks {
    private String detailsPageLink;
    private String homePageLink;
    private String courseJoinLink;
    
    public AdminSearchStudentLinks(final String detailsPageLink, final String homePageLink, final String courseJoinLink) {
        this.detailsPageLink = detailsPageLink;
        this.homePageLink = homePageLink;
        this.courseJoinLink = courseJoinLink;
    }

    public String getDetailsPageLink() {
        return detailsPageLink;
    }

    public String getHomePageLink() {
        return homePageLink;
    }

    public String getCourseJoinLink() {
        return courseJoinLink;
    }
}
