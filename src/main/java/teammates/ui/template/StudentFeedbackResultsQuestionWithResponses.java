package teammates.ui.template;

import java.util.List;
import java.util.stream.Collectors;

/**
 * View Model for student/feedbackResults/questionWithResponses.tag
 *
 * <p>Manages student feedback results questions details, self-responses tables and others-responses tables separately
 * for a specific question.
 */
public class StudentFeedbackResultsQuestionWithResponses {
    private FeedbackResultsQuestionDetails questionDetails;
    private List<FeedbackResultsResponseTable> responseTables;

    public StudentFeedbackResultsQuestionWithResponses(
                                    FeedbackResultsQuestionDetails questionDetails,
                                    List<FeedbackResultsResponseTable> responseTables) {
        this.questionDetails = questionDetails;
        this.responseTables = responseTables;
    }

    public FeedbackResultsQuestionDetails getQuestionDetails() {
        return questionDetails;
    }

    /**
     * Returns a list of responses which are provided by user himself.
     */
    public List<FeedbackResultsResponseTable> getSelfResponseTables() {
        return responseTables.stream().filter(responseTable -> responseTable.isGiverNameYou())
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of responses which are provided by others.
     */
    public List<FeedbackResultsResponseTable> getOthersResponseTables() {
        return responseTables.stream().filter(responseTable -> !responseTable.isGiverNameYou())
                .collect(Collectors.toList());
    }

    public boolean getIsSelfResponseTablesEmpty() {
        return getSelfResponseTables().isEmpty();
    }

}
