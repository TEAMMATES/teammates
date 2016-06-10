package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
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
    private boolean isTeamQuestion;
    
    public FeedbackSubmissionEditQuestion(FeedbackQuestionAttributes questionAttributes, int qnIndx,
                                    boolean isModeratedQuestion) {
        
        courseId = questionAttributes.courseId;
        questionNumber = questionAttributes.questionNumber;
        this.qnIndx = qnIndx;
        questionId = questionAttributes.getId();
        questionText = questionAttributes.getQuestionDetails().getQuestionText();
        visibilityMessages = questionAttributes.getVisibilityMessage();
        questionType = questionAttributes.questionType;
        numberOfEntitiesToGiveFeedbackTo = questionAttributes.numberOfEntitiesToGiveFeedbackTo;
        this.isModeratedQuestion = isModeratedQuestion;
        isRecipientNameHidden = questionAttributes.isRecipientNameHidden();
        isTeamQuestion = questionAttributes.giverType.equals(FeedbackParticipantType.TEAMS);
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
    
    public boolean isTeamQuestion() {
        return isTeamQuestion;
    }
}
