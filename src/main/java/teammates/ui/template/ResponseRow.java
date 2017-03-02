package teammates.ui.template;

import java.util.List;

public class ResponseRow {
    private String giverName;
    private String recipientName;
    private String response;
    private List<FeedbackResponseCommentRow> feedbackResponseComments;

    public ResponseRow(String giverName, String recipientName,
                       String response, List<FeedbackResponseCommentRow> feedbackResponseComments) {
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

    public List<FeedbackResponseCommentRow> getFeedbackResponseComments() {
        return feedbackResponseComments;
    }
}
