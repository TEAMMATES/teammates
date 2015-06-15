package teammates.ui.template;

import teammates.common.datatransfer.CommentAttributes;

public class InstructorCommentsCommentRow extends CommentRow{
    private Boolean isInstructorAllowedToModifyCommentInSection;
    private String typeOfPeopleCanViewComment;
    private String editedAt;
    private VisibilityCheckboxes visibilityCheckboxes;
    
    // Visibility Settings
    private String showCommentsTo;
    private String showGiverNameTo;
    private String showRecipientNameTo;
    
    public InstructorCommentsCommentRow(String giverDetails, CommentAttributes comment, String recipientDetails,
                   String creationTime, Boolean isInstructorAllowedToModifyCommentInSection,
                   String typeOfPeopleCanViewComment, String editedAt, VisibilityCheckboxes visibilityCheckboxes,
                   String showCommentsTo, String showGiverNameTo, String showRecipientNameTo) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.recipientDetails = recipientDetails;
        this.creationTime = creationTime;
        this.isInstructorAllowedToModifyCommentInSection = isInstructorAllowedToModifyCommentInSection;
        this.typeOfPeopleCanViewComment = typeOfPeopleCanViewComment;
        this.editedAt = editedAt;
        this.visibilityCheckboxes = visibilityCheckboxes;
        
        this.showCommentsTo = showCommentsTo;
        this.showGiverNameTo = showGiverNameTo;
        this.showRecipientNameTo = showRecipientNameTo;
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
