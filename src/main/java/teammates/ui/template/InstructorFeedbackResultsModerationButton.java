package teammates.ui.template;

import teammates.common.datatransfer.FeedbackQuestionAttributes;

public class InstructorFeedbackResultsModerationButton {
    
    private boolean isDisabled;
    private String buttonText;
    private int questionNumber;
    private String className;
    private String giverIdentifier;
    private String courseId;
    private String feedbackSessionName;
    
    public InstructorFeedbackResultsModerationButton(boolean isDisabled, String className,
                             String giverIdentifier,
                             String courseId, String feedbackSessionName, FeedbackQuestionAttributes question,
                             String buttonText) {
        
        this.isDisabled = isDisabled;
        this.className = className;
        this.questionNumber = question != null ? question.questionNumber : -1;
        this.giverIdentifier = giverIdentifier;
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        this.buttonText = buttonText;
        
    }
    
    public int getQuestionNumber() {
        return questionNumber;
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
    
    
}
