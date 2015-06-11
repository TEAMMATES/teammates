package teammates.ui.template;

import java.util.List;

public class ResponseRow {
    private String giverName;
    private String recipientName;
    private String response;
    private List<FeedbackResponseCommentRow> feedbackResponseCommentRows;
    
    public ResponseRow(String giverName, String recipientName, 
                           String response, List<FeedbackResponseCommentRow> feedbackResponseCommentRows) {
        this.giverName = giverName;
        this.recipientName = recipientName;
        this.response = response;
        this.feedbackResponseCommentRows = feedbackResponseCommentRows;
    }
    
    public String getGiverName() {
        return giverName;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public String getResponse() {
        return response;
    }
    
    public List<FeedbackResponseCommentRow> getFeedbackResponseCommentRows() {
        return feedbackResponseCommentRows;
    }
    
    public boolean isCommentsEmpty() {
        return feedbackResponseCommentRows == null || feedbackResponseCommentRows.size() <= 0;
    }
}
