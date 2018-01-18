package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

public class InstructorFeedbackResultsResponsePanel {
    private FeedbackQuestionAttributes question;
    private FeedbackResponseAttributes response;

    private String questionText;
    private String additionalInfoText;

    private ElementTag rowAttributes;

    private String displayableResponse;

    private List<FeedbackResponseCommentRow> comments;
    private FeedbackResponseCommentRow frcForAdding;
    private boolean isAllowedToAddComment;
    private boolean isCommentsOnResponsesAllowed;

    // The indexes are used for the parameters of js functions for handling response comments
    private int sectionId;
    private int recipientIndex;
    private int giverIndex;
    private int qnIndex; // TODO  investigate using question number instead of tracking an index

    public InstructorFeedbackResultsResponsePanel(FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
                                                  String questionText, int sectionId, String additionalInfoText,
                                                  ElementTag rowAttributes,
                                                  String displayableResponse,
                                                  List<FeedbackResponseCommentRow> comments,
                                                  boolean isAllowedToAddComment,
                                                  boolean isCommentsOnResponsesAllowed) {
        this.question = question;
        this.response = response;
        this.questionText = questionText;
        this.additionalInfoText = additionalInfoText;
        this.sectionId = sectionId;
        this.rowAttributes = rowAttributes;
        this.displayableResponse = displayableResponse;
        this.comments = comments;
        this.isAllowedToAddComment = isAllowedToAddComment;
        this.isCommentsOnResponsesAllowed = isCommentsOnResponsesAllowed;
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

    public List<FeedbackResponseCommentRow> getComments() {
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

    public void setFrcForAdding(FeedbackResponseCommentRow frcForAdding) {
        this.frcForAdding = frcForAdding;
    }

    public FeedbackResponseCommentRow getFrcForAdding() {
        return frcForAdding;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public boolean isCommentsOnResponsesAllowed() {
        return isCommentsOnResponsesAllowed;
    }
}
