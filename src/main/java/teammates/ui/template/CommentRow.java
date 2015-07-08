package teammates.ui.template;

import teammates.common.datatransfer.CommentAttributes;

public class CommentRow {
    protected String giverDetails;
    protected CommentAttributes comment;
    protected String recipientDetails;
    protected String creationTime;
    protected String editedAt;
    protected ElementTag editButton;
   
    public CommentRow() {
        
    }
    
    public CommentRow(String giverDetails, CommentAttributes comment,
                      String recipientDetails, String creationTime, String editedAt, ElementTag editButton) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.recipientDetails = recipientDetails;
        this.creationTime = creationTime;
        this.editedAt = editedAt;
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
    
    public String getEditedAt() {
        return editedAt;
    }
    
    public ElementTag getEditButton() {
        return editButton;
    }
}
