package teammates.ui.template;

import teammates.common.datatransfer.CommentAttributes;

public class CommentRow {
    private String giverDetails;
    private CommentAttributes comment;
    private String recipientDetails;
    private String creationTime;
    private ElementTag editButton;
    private Boolean isInstructorAllowedToModifyCommentInSection;
    private String typeOfPeopleCanViewComment;
    private String editedAt;
    private VisibilityCheckboxes visibilityCheckboxes;
    
    // Visibility Settings
    private String showCommentsTo;
    private String showGiverNameTo;
    private String showRecipientNameTo;
    
    public CommentRow(String giverDetails, CommentAttributes comment, String recipientDetails, String creationTime,
                                    ElementTag editButton, Boolean isInstructorAllowedToModifyCommentInSection,
                                    String typeOfPeopleCanViewComment, String editedAt, VisibilityCheckboxes visibilityCheckboxes,
                                    String showCommentsTo, String showGiverNameTo, String showRecipientNameTo) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.recipientDetails = recipientDetails;
        this.creationTime = creationTime;
        this.editButton = editButton;
        this.isInstructorAllowedToModifyCommentInSection = isInstructorAllowedToModifyCommentInSection;
        this.typeOfPeopleCanViewComment = typeOfPeopleCanViewComment;
        this.editedAt = editedAt;
        this.visibilityCheckboxes = visibilityCheckboxes;
        
        this.showCommentsTo = showCommentsTo;
        this.showGiverNameTo = showGiverNameTo;
        this.showRecipientNameTo = showRecipientNameTo;
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
    
    public boolean isInstructorAllowedToModifyCommentInSection() {
        return isInstructorAllowedToModifyCommentInSection;
    }
    
    public String getTypeOfPeopleCanViewComment() {
        return typeOfPeopleCanViewComment;
    }
    
    public String getEditedAt() {
        return editedAt;
    }
    
    public VisibilityCheckboxes getVisibilityCheckboxes() {
        return visibilityCheckboxes;
    }
    
    public String getShowCommentsTo() {
        return showCommentsTo;
    }
    
    public String getShowGiverNameTo() {
        return showGiverNameTo;
    }
    
    public String getShowRecipientNameTo() {
        return showRecipientNameTo;
    }
}
