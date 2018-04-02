package teammates.ui.template;

public class AdminFeedbackSessionRow {

    private String sessionStatusForShow;
    private String feedbackSessionStatsLink;
    private String sessionStartTime;
    private String sessionStartTimeIso8601Utc;
    private String sessionEndTime;
    private String sessionEndTimeIso8601Utc;
    private String instructorHomePageViewLink;
    private String creatorEmail;
    private String courseId;
    private String feedbackSessionName;

    public AdminFeedbackSessionRow(String sessionStatusForShow, String feedbackSessionStatsLink,
            String sessionStartTime, String sessionStartTimeIso8601Utc, String sessionEndTime,
            String sessionEndTimeIso8601Utc, String instructorHomePageViewLink, String creatorEmail,
            String courseId, String feedbackSessionName) {
        this.sessionStatusForShow = sessionStatusForShow;
        this.feedbackSessionStatsLink = feedbackSessionStatsLink;
        this.sessionStartTime = sessionStartTime;
        this.sessionStartTimeIso8601Utc = sessionStartTimeIso8601Utc;
        this.sessionEndTime = sessionEndTime;
        this.sessionEndTimeIso8601Utc = sessionEndTimeIso8601Utc;
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

    public String getSessionStartTimeIso8601Utc() {
        return sessionStartTimeIso8601Utc;
    }

    public String getSessionEndTimeIso8601Utc() {
        return sessionEndTimeIso8601Utc;
    }
}
