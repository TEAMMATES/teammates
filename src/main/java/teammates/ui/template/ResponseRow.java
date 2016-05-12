package teammates.ui.template;

import java.util.List;

public class ResponseRow {
    private String giverName;
    private String recipientName;
    private String response;
    private List<FeedbackResponseComment> feedbackResponseComments;
    
    public ResponseRow(final String giverName, final String recipientName, 
                           final String response, final List<FeedbackResponseComment> feedbackResponseComments) {
        this.giverName = giverName;
        this.recipientName = recipientName;
        this.response = response;
        this.feedbackResponseComments = feedbackResponseComments;
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
    
    public List<FeedbackResponseComment> getFeedbackResponseComments() {
        return feedbackResponseComments;
    }
}
