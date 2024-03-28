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

    public FeedbackResponsesData() {
        responses = Collections.emptyList();
    }

    /**
     *  Creates FeedbackResponsesData from a list of FeedbackResponseAttributes.
     *  TODO: When deleting Attributes, rename createFromEntity to be constructor.
     */
    public static FeedbackResponsesData createFromAttributes(List<FeedbackResponseAttributes> responses) {
        return new FeedbackResponsesData(responses.stream().map(FeedbackResponseData::new).collect(Collectors.toList()));
    }

    /**
     *  Creates FeedbackResponsesData from a list of FeedbackResponse.
     */
    public static FeedbackResponsesData createFromEntity(List<FeedbackResponse> responses) {
        return new FeedbackResponsesData(responses.stream().map(FeedbackResponseData::new).collect(Collectors.toList()));
    }

    public void setResponses(List<FeedbackResponseData> responses) {
        this.responses = responses;
    }

    public List<FeedbackResponseData> getResponses() {
        return responses;
    }
}
