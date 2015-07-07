package teammates.ui.template;

import java.util.List;

public class FeedbackResultsResponse {
    private String giverName;
    private String answer;
    private List<FeedbackResponseComment> comments;
    
    public FeedbackResultsResponse(String giverName, String answer,
                                    List<FeedbackResponseComment> comments) {
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

    public List<FeedbackResponseComment> getComments() {
        return comments;
    }   
}
