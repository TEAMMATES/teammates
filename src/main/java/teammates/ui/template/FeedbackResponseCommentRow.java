package teammates.ui.template;

public class FeedbackResponseCommentRow {
    private String giverDetails;
    private String comment;
    private String creationTime;
    private String editedAtText;
    private ElementTag editButton;

    public FeedbackResponseCommentRow(String giverDetails, String comment, String creationTime,
                                      ElementTag editButton, String editedAtText) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.creationTime = creationTime;
        this.editButton = editButton;
        this.editedAtText = editedAtText;
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

    public String getEditedAtText() {
        return editedAtText;
    }

    public ElementTag getEditButton() {
        return editButton;
    }
}
