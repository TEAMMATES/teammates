package teammates.ui.template;

import teammates.common.datatransfer.CommentAttributes;

public class StudentCommentsCommentRow extends CommentRow {
    private String editedAt;
    
    public StudentCommentsCommentRow(String giverDetails, CommentAttributes comment, String recipientDetails, 
                                     String creationTime, String editedAt) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.recipientDetails = recipientDetails;
        this.creationTime = creationTime;
        this.editedAt = editedAt;
        
    }
    
    public String getEditedAt() {
        return editedAt;
    }
}
