package teammates.ui.template;

public class InstructorHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private boolean isRecent;
    private String href;
    private InstructorFeedbackSessionActions actions;
    
    public InstructorHomeFeedbackSessionRow(String name, String tooltip, String status,
            String href, boolean isRecent, InstructorFeedbackSessionActions actions) {
        super(name, tooltip, status);
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

    public InstructorFeedbackSessionActions getActions() {
        return actions;
    }
}