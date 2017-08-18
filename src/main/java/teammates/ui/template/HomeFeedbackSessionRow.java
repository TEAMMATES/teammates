package teammates.ui.template;

public class HomeFeedbackSessionRow {
    private String name;
    private String tooltip;
    private String submissionStatus;
    private String publishedStatus;

    public HomeFeedbackSessionRow(String name, String tooltip, String submissionStatus, String publishedStatus) {
        this.name = name;
        this.tooltip = tooltip;
        this.submissionStatus = submissionStatus;
        this.publishedStatus = publishedStatus;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public String getPublishedStatus() {
        return publishedStatus;
    }
}
