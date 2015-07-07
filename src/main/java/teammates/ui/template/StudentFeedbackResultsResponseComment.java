package teammates.ui.template;

import java.util.Date;

public class StudentFeedbackResultsResponseComment {
    private Long frcId;
    private String giverEmail;
    private Date createdAt;
    private String editedAtText;
    private String commentText;
    
    public StudentFeedbackResultsResponseComment(Long frcId, String giverEmail, Date createdAt, String editedAtText,
                                                 String commentText) {
        this.frcId = frcId;
        this.giverEmail = giverEmail;
        this.createdAt = createdAt;
        this.editedAtText = editedAtText;
        this.commentText = commentText;
    }
    
    public Long getFrcId() {
        return frcId;
    }

    public String getGiverEmail() {
        return giverEmail;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }

    public String getEditedAtText() {
        return editedAtText;
    }
    
    public String getCommentText() {
        return commentText;
    }
}
