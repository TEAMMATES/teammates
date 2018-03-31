package teammates.ui.template;

public class AdminSearchStudentLinks {
    private String detailsPageLink;
    private String homePageLink;
    private String courseJoinLink;

    public AdminSearchStudentLinks(String detailsPageLink, String homePageLink, String courseJoinLink) {
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
