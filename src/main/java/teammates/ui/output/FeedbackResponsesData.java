package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

/**
 * The API output format of a list of {@link FeedbackResponseAttributes}.
 */
public class FeedbackResponsesData extends ApiOutput {

    private List<FeedbackResponseData> responses;

    public FeedbackResponsesData(List<FeedbackResponseAttributes> responses) {
        this.responses = responses.stream().map(FeedbackResponseData::new).collect(Collectors.toList());
    }

    public List<FeedbackResponseData> getResponses() {
        return responses;
    }
}
