package teammates.ui.template;

import java.util.List;

public class StudentFeedbackResultsQuestionWithResponses {
    private FeedbackResultsQuestionDetails questionDetails;
    private List<FeedbackResultsResponseTable> responseTables;
    
    public StudentFeedbackResultsQuestionWithResponses(
                                    final FeedbackResultsQuestionDetails questionDetails, 
                                    final List<FeedbackResultsResponseTable> responseTables) {
        this.questionDetails = questionDetails;
        this.responseTables = responseTables;
    }

    public FeedbackResultsQuestionDetails getQuestionDetails() {
        return questionDetails;
    }

    public List<FeedbackResultsResponseTable> getResponseTables() {
        return responseTables;
    }

}
