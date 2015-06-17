package teammates.ui.template;

public class ModerationsButton {

    private boolean isAllowedToModerate;
    private int questionNumber;
    private String giverIdentifier;
    private String courseId;
    private String feedbackSessionName;
    
    public ModerationsButton(boolean isAllowedToModerate, int questionNumber, String giverIdentifier,
                             String courseId, String feedbackSessionName) {
        this.isAllowedToModerate = isAllowedToModerate;
        this.questionNumber = questionNumber;
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
    
}
