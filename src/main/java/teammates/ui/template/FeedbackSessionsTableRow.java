package teammates.ui.template;

public class FeedbackSessionsTableRow {
    private String courseId;
    private String name;
    private String submissionsTooltip;
    private String publishedTooltip;
    private String href;
    private String submissionStatus;
    private String publishedStatus;
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
    public FeedbackSessionsTableRow(String courseId, String name, String submissionsTooltip, String publishedTooltip,
                                    String submissionStatus, String publishedStatus, String href,
                                    InstructorFeedbackSessionActions actions, ElementTag attributes) {
        this.courseId = courseId;
        this.name = name;
        this.submissionsTooltip = submissionsTooltip;
        this.publishedTooltip = publishedTooltip;
        this.href = href;
        this.submissionStatus = submissionStatus;
        this.publishedStatus = publishedStatus;
        this.actions = actions;
        this.rowAttributes = attributes;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public String getPublishedStatus() {
        return publishedStatus;
    }

    public String getSubmissionsTooltip() {
        return submissionsTooltip;
    }

    public String getPublishedTooltip() {
        return publishedTooltip;
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
