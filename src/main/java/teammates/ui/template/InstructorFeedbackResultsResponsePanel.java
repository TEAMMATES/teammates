package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;

public class InstructorFeedbackResultsResponsePanel {
    private FeedbackQuestionAttributes question;
    private FeedbackResponseAttributes response;
    
    private String questionText;
    private String additionalInfoText;
    
    private ElementTag rowAttributes;
    
    private String displayableResponse;
    
    private List<FeedbackResponseComment> comments;
    private FeedbackResponseComment frcForAdding;
    private boolean isAllowedToAddComment;
    
    // The indexes are used for the parameters of js functions for handling response comments 
    private int recipientIndex;
    private int giverIndex;
    private int qnIndex; // TODO  investigate using question number instead of tracking an index
    
    public InstructorFeedbackResultsResponsePanel(FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
                                                  String questionText, String additionalInfoText, 
                                                  ElementTag rowAttributes, 
                                                  String displayableResponse, 
                                                  List<FeedbackResponseComment> comments, boolean isAllowedToAddComment) {
        this.question = question;
        this.response = response;
        this.questionText = questionText;
        this.additionalInfoText = additionalInfoText;
        this.rowAttributes = rowAttributes;
        this.displayableResponse = displayableResponse;
        this.comments = comments;
        this.isAllowedToAddComment = isAllowedToAddComment;
    }
    
    public void setCommentsIndexes(int recipientIndex, int giverIndex, int qnIndex) {
        this.recipientIndex = recipientIndex;
        this.giverIndex = giverIndex;
        this.qnIndex = qnIndex;
    }

    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }
    
    public FeedbackResponseAttributes getResponse() {
        return response;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAdditionalInfoText() {
        return additionalInfoText;
    }

    public ElementTag getRowAttributes() {
        return rowAttributes;
    }

    public String getDisplayableResponse() {
        return displayableResponse;
    }
    
    public List<FeedbackResponseComment> getComments() {
        return comments;
    }

    public boolean isAllowedToAddComment() {
        return isAllowedToAddComment;
    }

    
    public int getRecipientIndex() {
        return recipientIndex;
    }

    public int getGiverIndex() {
        return giverIndex;
    }

    public int getQnIndex() {
        return qnIndex;
    }
    

    public void setFrcForAdding(FeedbackResponseComment frcForAdding) {
        this.frcForAdding = frcForAdding;
    }

    public FeedbackResponseComment getFrcForAdding() {
        return frcForAdding;
    }
    
}
