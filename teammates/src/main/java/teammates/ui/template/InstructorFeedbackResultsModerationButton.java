package teammates.ui.template;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

public class InstructorFeedbackResultsModerationButton {

    private boolean isDisabled;
    private String buttonText;
    private String questionId;
    private String className;
    private String giverIdentifier;
    private String courseId;
    private String feedbackSessionName;
    private String moderateFeedbackResponseLink;

    public InstructorFeedbackResultsModerationButton(boolean isDisabled, String className,
                             String giverIdentifier,
                             String courseId, String feedbackSessionName, FeedbackQuestionAttributes question,
                             String buttonText, String moderateFeedbackResponseLink) {

        this.isDisabled = isDisabled;
        this.className = className;
        this.questionId = question == null ? null : question.getId();
        this.giverIdentifier = giverIdentifier;
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.buttonText = buttonText;
        this.moderateFeedbackResponseLink = moderateFeedbackResponseLink;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getGiverIdentifier() {
        return giverIdentifier;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public String getClassName() {
        return className;
    }

    public String getButtonText() {
        return buttonText;
    }

    /**
     * Retrieves the link to moderate the feedback.
     * @return the link to the feedback edit page
     */
    public String getModerateFeedbackResponseLink() {
        return moderateFeedbackResponseLink;
    }
}
