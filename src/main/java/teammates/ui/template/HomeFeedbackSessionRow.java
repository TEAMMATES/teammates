package teammates.ui.template;

public class HomeFeedbackSessionRow {
    private String name;
    private String tooltip;
    private String status;
    
    public HomeFeedbackSessionRow(final String name, final String tooltip, final String status) {
        this.name = name;
        this.tooltip = tooltip;
        this.status = status;
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
}
