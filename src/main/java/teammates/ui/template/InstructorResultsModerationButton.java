package teammates.ui.template;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class InstructorResultsModerationButton {
    
    private boolean isAllowedToModerate;
    private boolean isDisabled;
    private int questionNumber;
    private String giverIdentifier;
    private String courseId;
    private String feedbackSessionName;
    
    public InstructorResultsModerationButton(boolean isAllowedToModerate, boolean isDisabled, 
                             String giverIdentifier,
                             String courseId, String feedbackSessionName, FeedbackQuestionAttributes question) {
        this.isAllowedToModerate = isAllowedToModerate;
        this.isDisabled = isDisabled;
        this.questionNumber = question != null ? question.questionNumber : -1;
        this.giverIdentifier = giverIdentifier;
        this.courseId = courseId;
        this.feedbackSessionName = feedbackSessionName;
        
    }
    
    public boolean isAllowedToModerate() {
        return isAllowedToModerate;
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
    
}
