package teammates.ui.template;

import java.util.List;

public class FeedbackResultsResponse {
    private String giverName;
    private String answer;
    private List<FeedbackResponseCommentRow> comments;

    public FeedbackResultsResponse(String giverName, String answer,
                                   List<FeedbackResponseCommentRow> comments) {
        this.giverName = giverName;
        this.answer = answer;
        this.comments = comments;
    }

    public String getGiverName() {
        return giverName;
    }

    public String getAnswer() {
        return answer;
    }

    public List<FeedbackResponseCommentRow> getComments() {
        return comments;
    }
}
