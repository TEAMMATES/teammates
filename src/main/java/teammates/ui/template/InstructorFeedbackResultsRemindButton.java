package teammates.ui.template;

public class InstructorFeedbackResultsRemindButton {

    private boolean isDisabled;
    private String buttonText;
    private String className;
    private String courseId;
    private String feedbackSessionName;
    private String urlLink;

    public InstructorFeedbackResultsRemindButton(boolean isDisabled, String className,
            String courseId, String feedbackSessionName,
            String buttonText, String urlLink) {

        this.isDisabled = isDisabled;
        this.className = className;
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.buttonText = buttonText;
        this.urlLink = urlLink;
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
     * Returns the URL that should be linked to the button.
     */
    public String getUrlLink() {
        return urlLink;
    }
}
