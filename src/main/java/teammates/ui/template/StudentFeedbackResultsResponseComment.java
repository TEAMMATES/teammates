package teammates.ui.template;

import java.util.Date;

public class StudentFeedbackResultsResponseComment {
    private String giverEmail;
    private Date createdAt;
    private String commentText;
    
    public StudentFeedbackResultsResponseComment(String giverEmail, Date createdAt, String commentText) {
        this.giverEmail = giverEmail;
        this.createdAt = createdAt;
        this.commentText = commentText;
    }
    
    public String getGiverEmail() {
        return giverEmail;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public String getCommentText() {
        return commentText;
    }
}
