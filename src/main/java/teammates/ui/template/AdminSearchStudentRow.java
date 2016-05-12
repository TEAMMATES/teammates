package teammates.ui.template;

import java.util.List;

public class AdminSearchStudentRow {
    private String id;
    private String name;
    private String institute;
    private String courseName;
    private String courseId;
    private String section;
    private String team;
    private String googleId;
    private String email;
    private String comments;
    private String viewRecentActionsId;
    
    private AdminSearchStudentLinks links;
    
    private List<AdminSearchStudentFeedbackSession> openFeedbackSessions;
    private List<AdminSearchStudentFeedbackSession> closedFeedbackSessions;
    private List<AdminSearchStudentFeedbackSession> publishedFeedbackSessions;
    
    public AdminSearchStudentRow(final String id, final String name, final String institute, final String courseName,
                                 final String courseId, final String section, final String team, final String googleId,
                                 final String email, final String comments, final String viewRecentActionsId,
                                 final AdminSearchStudentLinks links,
                                 final List<AdminSearchStudentFeedbackSession> openFeedbackSessions,
                                 final List<AdminSearchStudentFeedbackSession> closedFeedbackSessions,
                                 final List<AdminSearchStudentFeedbackSession> publishedFeedbackSessions) {
        this.id = id;
        this.name = name;
        this.institute = institute;
        this.courseName = courseName;
        this.courseId = courseId;
        this.section = section;
        this.team = team;
        this.googleId = googleId;
        this.email = email;
        this.comments = comments;
        this.viewRecentActionsId = viewRecentActionsId;
        this.links = links;
        this.openFeedbackSessions = openFeedbackSessions;
        this.closedFeedbackSessions = closedFeedbackSessions;
        this.publishedFeedbackSessions = publishedFeedbackSessions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstitute() {
        return institute;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSection() {
        return section;
    }

    public String getTeam() {
        return team;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getEmail() {
        return email;
    }

    public String getComments() {
        return comments;
    }

    public String getViewRecentActionsId() {
        return viewRecentActionsId;
    }

    public AdminSearchStudentLinks getLinks() {
        return links;
    }

    public List<AdminSearchStudentFeedbackSession> getOpenFeedbackSessions() {
        return openFeedbackSessions;
    }

    public List<AdminSearchStudentFeedbackSession> getClosedFeedbackSessions() {
        return closedFeedbackSessions;
    }

    public List<AdminSearchStudentFeedbackSession> getPublishedFeedbackSessions() {
        return publishedFeedbackSessions;
    }
}
