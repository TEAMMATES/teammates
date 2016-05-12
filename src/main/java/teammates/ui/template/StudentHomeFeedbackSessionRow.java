package teammates.ui.template;

public class StudentHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private String endTime;
    private StudentFeedbackSessionActions actions;
    private int index;

    public StudentHomeFeedbackSessionRow(final String name, final String tooltip, final String status,
            final String endTime, final StudentFeedbackSessionActions actions, final int index) {
        super(name, tooltip, status);
        this.endTime = endTime;
        this.actions = actions;
        this.index = index;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public StudentFeedbackSessionActions getActions() {
        return actions;
    }
    
    public int getIndex() {
        return index;
    }
}