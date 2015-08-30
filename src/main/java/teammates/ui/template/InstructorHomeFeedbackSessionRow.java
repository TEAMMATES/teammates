package teammates.ui.template;

public class InstructorHomeFeedbackSessionRow extends HomeFeedbackSessionRow {
    private String recent;
    private String href;
    private InstructorFeedbackSessionActions actions;
    
    public InstructorHomeFeedbackSessionRow(String name, String tooltip, String status,
            String href, String recent, InstructorFeedbackSessionActions actions) {
        super(name, tooltip, status);
        this.recent = recent;
        this.href = href;
        this.actions = actions;
    }
    
    public String getRecent() {
        return recent;
    }
    
    public String getHref() {
        return href;
    }

    public InstructorFeedbackSessionActions getActions() {
        return actions;
    }
}