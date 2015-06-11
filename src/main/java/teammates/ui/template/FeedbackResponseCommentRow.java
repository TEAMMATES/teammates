package teammates.ui.template;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;

public class FeedbackResponseCommentRow {
    private String giverDetails;
    private FeedbackResponseCommentAttributes comment;
    private String creationTime;
    private ElementTag editButton;

    public FeedbackResponseCommentRow(String giverDetails, FeedbackResponseCommentAttributes comment,
                                         String creationTime, ElementTag editButton) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.creationTime = creationTime;
        this.editButton = editButton;
    }

    public String getGiverDetails() {
        return giverDetails;
    }

    public FeedbackResponseCommentAttributes getComment() {
        return comment;
    }
    
    public String getCommentText() {
        return comment.commentText.getValue();
    }

    public String getCreationTime() {
        return creationTime;
    }

    public ElementTag getEditButton() {
        return editButton;
    }
}
