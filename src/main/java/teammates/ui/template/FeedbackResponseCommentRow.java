package teammates.ui.template;

public class FeedbackResponseCommentRow {
    protected String giverDetails;
    protected String comment;
    protected String creationTime;
    private ElementTag editButton;
    
    public FeedbackResponseCommentRow() {
        
    }
    
    public FeedbackResponseCommentRow(String giverDetails, String comment,
                                         String creationTime, ElementTag editButton) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.creationTime = creationTime;
        this.editButton = editButton;
    }

    public String getGiverDetails() {
        return giverDetails;
    }

    public String getComment() {
        return comment;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public ElementTag getEditButton() {
        return editButton;
    }
}
