package teammates.ui.template;

public class HomeFeedbackSessionRow {
    private String name;
    private String submissionsTooltip;
    private String publishedTooltip;
    private String submissionStatus;
    private String publishedStatus;

    public HomeFeedbackSessionRow(String name, String submissionsTooltip, String publishedTooltip,
                                  String submissionStatus, String publishedStatus) {
        this.name = name;
        this.submissionsTooltip = submissionsTooltip;
        this.publishedTooltip = publishedTooltip;
        this.submissionStatus = submissionStatus;
        this.publishedStatus = publishedStatus;
    }

    public String getName() {
        return name;
    }

    public String getSubmissionsTooltip() {
        return submissionsTooltip;
    }

    public String getPublishedTooltip() {
        return publishedTooltip;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public String getPublishedStatus() {
        return publishedStatus;
    }
}
