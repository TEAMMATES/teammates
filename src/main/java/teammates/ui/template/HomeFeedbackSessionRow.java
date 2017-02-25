package teammates.ui.template;

public class HomeFeedbackSessionRow {
    private String name;
    private String tooltip;
    private String status;

    public HomeFeedbackSessionRow(String name, String tooltip, String status) {
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
