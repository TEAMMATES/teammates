package teammates.ui.template;

public class InstructorHomeFeedbackSessionRow {
    private String name;
    private String tooltip;
    private String status;
    private String href;
    private String recent;
    private InstructorFeedbackSessionActions actions;
    
    public InstructorHomeFeedbackSessionRow(String name, String tooltip, String status,
            String href, String recent, InstructorFeedbackSessionActions actions) {
        this.name = name;
        this.tooltip = tooltip;
        this.status = status;
        this.href = href;
        this.recent = recent;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getStatus() {
        return status;
    }

    public String getHref() {
        return href;
    }

    public String getRecent() {
        return recent;
    }

    public InstructorFeedbackSessionActions getActions() {
        return actions;
    }
}