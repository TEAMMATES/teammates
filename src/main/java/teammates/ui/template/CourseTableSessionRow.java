package teammates.ui.template;

public class CourseTableSessionRow {
    private String name;
    private String tooltip;
    private String status;
    private String href;
    private String recent;
    private String actions;
    
    /**
     * Constructs a session row for a course table.
     * @param name name of the session
     * @param tooltip tooltip displayed when hovering over status
     * @param status status of the session
     * @param href link for the session under response rate
     * @param recent if the session is considered recent (calculate response rate on load if true)
     * @param actions possible actions to do on the session, a block of HTML representing the formatted actions
     */
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