package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

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

    public List<FeedbackResultsResponseTable> getSelfResponseTables() {
        List<FeedbackResultsResponseTable> selfResponseTables = new ArrayList<>();
        for (int i = 0; i < responseTables.size(); i++) {
            if (responseTables.get(i).isGiverNameYou()) {
                selfResponseTables.add(responseTables.get(i));
            }
        }
        return selfResponseTables;
    }

    public List<FeedbackResultsResponseTable> getOthersResponseTables() {
        List<FeedbackResultsResponseTable> othersResponseTables = new ArrayList<>();
        for (int i = 0; i < responseTables.size(); i++) {
            if (!responseTables.get(i).isGiverNameYou()) {
                othersResponseTables.add(responseTables.get(i));
            }
        }
        return othersResponseTables;
    }

    public boolean isSelfResponseTablesEmpty() {
        return getSelfResponseTables().isEmpty();
    }

}
