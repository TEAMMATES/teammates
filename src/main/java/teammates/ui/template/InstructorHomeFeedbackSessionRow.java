package teammates.ui.template;

public class InstructorHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private String startTime;
    private String startTimeToolTip;
    private String startTimeIso8601Utc;
    private String endTime;
    private String endTimeToolTip;
    private String endTimeIso8601Utc;
    private String href;
    private InstructorFeedbackSessionActions actions;

    public InstructorHomeFeedbackSessionRow(String name, String submissionsTooltip, String publishedTooltip,
            String submissionStatus, String publishedStatus, String startTime, String startTimeIso8601Utc,
            String startTimeToolTip, String endTime, String endTimeIso8601Utc, String endTimeToolTip,
            String href, InstructorFeedbackSessionActions actions) {
        super(name, submissionsTooltip, publishedTooltip, submissionStatus, publishedStatus);
        this.startTime = startTime;
        this.startTimeToolTip = startTimeToolTip;
        this.startTimeIso8601Utc = startTimeIso8601Utc;
        this.endTime = endTime;
        this.endTimeToolTip = endTimeToolTip;
        this.endTimeIso8601Utc = endTimeIso8601Utc;
        this.href = href;
        this.actions = actions;
    }

    public String getHref() {
        return href;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public InstructorFeedbackSessionActions getActions() {
        return actions;
    }

    public String getStartTimeToolTip() {
        return startTimeToolTip;
    }

    public String getEndTimeToolTip() {
        return endTimeToolTip;
    }

    public String getStartTimeIso8601Utc() {
        return startTimeIso8601Utc;
    }

    public String getEndTimeIso8601Utc() {
        return endTimeIso8601Utc;
    }
}
