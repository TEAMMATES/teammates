package teammates.common.datatransfer;

/**
 * An enum that defines the different types of feedback sessions.
 */
public enum FeedbackSessionType {
    PRIVATE("PRIVATE"),
    STANDARD("STANDARD"),
    TEAM_EVALUATION("TEAMEVALUATION"),
    OPTIMIZED_TEAM_EVALUATION("OPTIMIZEDTEAMEVALUATION");

    private String feedbackSessionTemplateName;

    FeedbackSessionType(String feedbackSessionTemplateName) {
        this.feedbackSessionTemplateName = feedbackSessionTemplateName;
    }

    public String getFeedbackSessionTemplateName() {
        return feedbackSessionTemplateName;
    }
}
