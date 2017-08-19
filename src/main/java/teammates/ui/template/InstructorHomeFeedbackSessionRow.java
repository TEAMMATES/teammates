package teammates.ui.template;

public class InstructorHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private String startTime;
    private String startTimeToolTip;
    private String startTimeDateStamp;
    private String endTime;
    private String endTimeToolTip;
    private String endTimeDateStamp;
    private String href;
    private InstructorFeedbackSessionActions actions;

    public InstructorHomeFeedbackSessionRow(String name, String tooltip, String status,
            String startTime, String startTimeToolTip, String startTimeDateStamp, String endTime,
            String endTimeToolTip, String endTimeDateStamp, String href, InstructorFeedbackSessionActions actions) {
        super(name, tooltip, status);
        this.startTime = startTime;
        this.startTimeToolTip = startTimeToolTip;
        this.startTimeDateStamp = startTimeDateStamp;
        this.endTime = endTime;
        this.endTimeToolTip = endTimeToolTip;
        this.endTimeDateStamp = endTimeDateStamp;
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

    public String getStartTimeDateStamp() {
        return startTimeDateStamp;
    }

    public String getEndTimeDateStamp() {
        return endTimeDateStamp;
    }

}
