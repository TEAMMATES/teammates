package teammates.ui.output;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.FeedbackResponse;

/**
 * The API output format of a list of {@link FeedbackResponse}.
 */
public class FeedbackResponsesData extends ApiOutput {

    private List<FeedbackResponseData> responses;

    public FeedbackResponsesData(List<FeedbackResponse> responses) {
        this.responses = responses.stream().map(FeedbackResponseData::new).collect(Collectors.toList());
    }

    public FeedbackResponsesData() {
        responses = Collections.emptyList();
    }

    public void setResponses(List<FeedbackResponseData> responses) {
        this.responses = responses;
    }

    public List<FeedbackResponseData> getResponses() {
        return responses;
    }
}
