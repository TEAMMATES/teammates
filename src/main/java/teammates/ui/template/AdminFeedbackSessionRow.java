package teammates.ui.template;

public class AdminFeedbackSessionRow {

    private String sessionStatusForShow;
    private String feedbackSessionStatsLink;
    private String sessionStartTime;
    private String sessionStartTimeDateStamp;
    private String sessionEndTime;
    private String sessionEndTimeDateStamp;
    private String instructorHomePageViewLink;
    private String creatorEmail;
    private String courseId;
    private String feedbackSessionName;

    public AdminFeedbackSessionRow(String sessionStatusForShow, String feedbackSessionStatsLink,
                                    String sessionStartTime, String sessionStartTimeDateStamp,
                                    String sessionEndTime, String sessionEndTimeDateStamp,
                                    String instructorHomePageViewLink, String creatorEmail,
                                    String courseId, String feedbackSessionName) {
        this.sessionStatusForShow = sessionStatusForShow;
        this.feedbackSessionStatsLink = feedbackSessionStatsLink;
        this.sessionStartTime = sessionStartTime;
        this.sessionStartTimeDateStamp = sessionStartTimeDateStamp;
        this.sessionEndTime = sessionEndTime;
        this.sessionEndTimeDateStamp = sessionEndTimeDateStamp;
        this.instructorHomePageViewLink = instructorHomePageViewLink;
        this.creatorEmail = creatorEmail;
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getSessionStatusForShow() {
        return sessionStatusForShow;
    }

    public String getFeedbackSessionStatsLink() {
        return feedbackSessionStatsLink;
    }

    public String getSessionStartTime() {
        return sessionStartTime;
    }

    public String getSessionEndTime() {
        return sessionEndTime;
    }

    public String getInstructorHomePageViewLink() {
        return instructorHomePageViewLink;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isEndsWithTmt() {
        return creatorEmail.endsWith(".tmt");
    }

    public String getSessionStartTimeDateStamp() {
        return sessionStartTimeDateStamp;
    }

    public String getSessionEndTimeDateStamp() {
        return sessionEndTimeDateStamp;
    }
}
