package teammates.ui.template;

public class AdminSearchInstructorRow {
    private String id;
    private String name;
    private String courseName;
    private String courseId;
    private String googleId;
    private String googleIdLink;
    private String institute;
    private String viewRecentActionsId;
    private String email;
    private String courseJoinLink;
    
    public AdminSearchInstructorRow(final String id, final String name, final String courseName, final String courseId,
                                    final String googleId, final String googleIdLink, final String institute,
                                    final String viewRecentActionsId, final String email, final String courseJoinLink) {
        this.id = id;
        this.name = name;
        this.courseName = courseName;
        this.courseId = courseId;
        this.googleId = googleId;
        this.googleIdLink = googleIdLink;
        this.institute = institute;
        this.viewRecentActionsId = viewRecentActionsId;
        this.email = email;
        this.courseJoinLink = courseJoinLink;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getGoogleIdLink() {
        return googleIdLink;
    }

    public String getInstitute() {
        return institute;
    }

    public String getViewRecentActionsId() {
        return viewRecentActionsId;
    }

    public String getEmail() {
        return email;
    }

    public String getCourseJoinLink() {
        return courseJoinLink;
    }
}
