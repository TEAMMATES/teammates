package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

/**
 * The response of a list of feedback response.
 */
public class FeedbackResponses extends ApiOutput {

    List<FeedbackResponse> responses;

    public FeedbackResponses(List<FeedbackResponseAttributes> responses) {
        this.responses = responses.stream().map(FeedbackResponse::new).collect(Collectors.toList());
    }

    public List<FeedbackResponse> getResponses() {
        return responses;
    }
}
