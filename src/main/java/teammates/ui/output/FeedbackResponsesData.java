package teammates.ui.output;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.storage.sqlentity.FeedbackResponse;

/**
 * The API output format of a list of {@link FeedbackResponseAttributes}.
 */
public class FeedbackResponsesData extends ApiOutput {

    private List<FeedbackResponseData> responses;

    private FeedbackResponsesData(List<FeedbackResponseData> responses) {
        this.responses = responses;
    }

    // TODO: When deleting attributes, make constructor to be createFromEntity
    public static FeedbackResponsesData createFromAttributes(List<FeedbackResponseAttributes> responses) {
        return new FeedbackResponsesData(responses.stream().map(FeedbackResponseData::new).collect(Collectors.toList()));
    }

    public static FeedbackResponsesData createFromEntity(List<FeedbackResponse> responses) {
        return new FeedbackResponsesData(responses.stream().map(FeedbackResponseData::new).collect(Collectors.toList()));
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
