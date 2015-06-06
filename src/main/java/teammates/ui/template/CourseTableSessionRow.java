package teammates.ui.template;

public class CourseTableSessionRow {
    private String name;
    private String tooltip;
    private String status;
    private String href;
    private String recent;
    private String actions;
    
    public CourseTableSessionRow(String name, String tooltip, String status, String href, String recent, String actions) {
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
    public String getActions() {
        return actions;
    }
}