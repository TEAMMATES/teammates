package teammates.ui.template;

public class StudentCommentsFeedbackResponseCommentRow extends FeedbackResponseCommentRow {
    String editedAt;

    public StudentCommentsFeedbackResponseCommentRow(
            String giverDetails, String comment, String creationTime, String editedAt) {
        this.giverDetails = giverDetails;
        this.comment = comment;
        this.creationTime = creationTime;
        this.editedAt = editedAt;
    }

    public String getEditedAt() {
        return editedAt;
    }
}
