package teammates.ui.template;

public class FeedbackSessionsTableRow {
    private String courseId;
    private String name;
    private String tooltip;
    private String href;
    private String status;
    private InstructorFeedbackSessionActions actions;

    private ElementTag rowAttributes;

    /**
     * Constructs a session row for a course table.
     * @param name name of the session
     * @param tooltip tooltip displayed when hovering over status
     * @param status status of the session
     * @param href link for the session under response rate
     * @param actions possible actions to do on the session, a block of HTML representing the formatted actions
     */
    public FeedbackSessionsTableRow(String courseId, String name, String tooltip, String status, String href,
                                    InstructorFeedbackSessionActions actions, ElementTag attributes) {
        this.courseId = courseId;
        this.name = name;
        this.tooltip = tooltip;
        this.href = href;
        this.status = status;
        this.actions = actions;
        this.rowAttributes = attributes;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getHref() {
        return href;
    }

    public InstructorFeedbackSessionActions getActions() {
        return actions;
    }

    public ElementTag getRowAttributes() {
        return rowAttributes;
    }

}
