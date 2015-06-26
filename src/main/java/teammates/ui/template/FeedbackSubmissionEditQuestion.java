package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionType;

public class FeedbackSubmissionEditQuestion {
    private String courseId;
    private int questionNumber;
    private int qnIndx; // If not showing real question number
    private String questionId;
    private String questionText;
    private List<String> visibilityMessages;
    private FeedbackQuestionType questionType;
    private int numberOfEntitiesToGiveFeedbackTo;
    private boolean isModeratedQuestion;
    private boolean isRecipientNameHidden;
    
    public FeedbackSubmissionEditQuestion(String courseId, int questionNumber, int qnIndx, String questionId,
                                    String questionText, List<String> visibilityMessages,
                                    FeedbackQuestionType questionType, int numberOfEntitiesToGiveFeedbackTo,
                                    boolean isModeratedQuestion, boolean isRecipientNameHidden) {
        this.courseId = courseId;
        this.questionNumber = questionNumber;
        this.qnIndx = qnIndx;
        this.questionId = questionId;
        this.questionText = questionText;
        this.visibilityMessages = visibilityMessages;
        this.questionType = questionType;
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
        this.isModeratedQuestion = isModeratedQuestion;
        this.isRecipientNameHidden = isRecipientNameHidden;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public int getQuestionNumber() {
        return questionNumber;
    }
    
    public int getQnIndx() {
        return qnIndx;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public List<String> getVisibilityMessages() {
        return visibilityMessages;
    }
    
    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }
    
    public int getNumberOfEntitiesToGiveFeedbackTo() {
        return numberOfEntitiesToGiveFeedbackTo;
    }
    
    public boolean isModeratedQuestion() {
        return isModeratedQuestion;
    }
    
    public boolean isRecipientNameHidden() {
        return isRecipientNameHidden;
    }
}
