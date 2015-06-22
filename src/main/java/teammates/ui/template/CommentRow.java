package teammates.ui.template;

import teammates.common.datatransfer.CommentAttributes;

public class CommentRow {
    private String giverDetails;
    private CommentAttributes comment;
    private String recipientDetails;
    private String creationTime;
    private ElementTag editButton;
    
    public CommentRow(String giverDetails, CommentAttributes comment,
                       String recipientDetails, String creationTime, ElementTag editButton) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.recipientDetails = recipientDetails;
        this.creationTime = creationTime;
        this.editButton = editButton;
    }
    
    public String getGiverDetails() {
        return giverDetails;
    }
    
    public CommentAttributes getComment() {
        return comment;
    }
    
    public String getRecipientDetails() {
        return recipientDetails;
    }
    
    public String getCreationTime() {
        return creationTime;
    }
    
    public ElementTag getEditButton() {
        return editButton;
    }
}
