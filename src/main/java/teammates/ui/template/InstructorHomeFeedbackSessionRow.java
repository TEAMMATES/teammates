package teammates.ui.template;

public class InstructorHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private boolean isRecent;
    private String startTime;
    private String endTime;
    private String href;
    private InstructorFeedbackSessionActions actions;

    public InstructorHomeFeedbackSessionRow(final String name, final String tooltip, final String status,
            final String startTime, final String endTime, final String href, final boolean isRecent,
            final InstructorFeedbackSessionActions actions) {
        super(name, tooltip, status);
        this.startTime = startTime;
        this.endTime = endTime;
        this.isRecent = isRecent;
        this.href = href;
        this.actions = actions;
    }

    public boolean isRecent() {
        return isRecent;
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
}