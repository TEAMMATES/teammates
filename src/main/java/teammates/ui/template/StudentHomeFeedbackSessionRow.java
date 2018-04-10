package teammates.ui.template;

public class StudentHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private String endTime;
    private String endTimeIso8601Utc;
    private StudentFeedbackSessionActions actions;
    private int index;

    public StudentHomeFeedbackSessionRow(String name, String submissionsTooltip, String publishedTooltip,
            String submissionStatus, String publishedStatus, String endTime, String endTimeIso8601Utc,
            StudentFeedbackSessionActions actions, int index) {
        super(name, submissionsTooltip, publishedTooltip, submissionStatus, publishedStatus);
        this.endTime = endTime;
        this.endTimeIso8601Utc = endTimeIso8601Utc;
        this.actions = actions;
        this.index = index;
    }

    public String getEndTime() {
        return endTime;
    }

    public StudentFeedbackSessionActions getActions() {
        return actions;
    }

    public String getEndTimeIso8601Utc() {
        return endTimeIso8601Utc;
    }

    public int getIndex() {
        return index;
    }
}
