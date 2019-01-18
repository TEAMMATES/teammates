package teammates.ui.newcontroller;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;

/**
 * Data transfer objects for {@link FeedbackResponseAttributes} between controller and HTTP.
 */
public class FeedbackResponseInfo {

    /**
     * The response of a list of feedback response.
     */
    public static class FeedbackResponsesResponse extends ActionResult.ActionOutput {

        List<FeedbackResponseResponse> responses;

        public FeedbackResponsesResponse(List<FeedbackResponseAttributes> responses) {
            this.responses = responses.stream().map(FeedbackResponseResponse::new).collect(Collectors.toList());
        }

        public List<FeedbackResponseResponse> getResponses() {
            return responses;
        }
    }

    /**
     * The response of a feedback response.
     */
    public static class FeedbackResponseResponse extends ActionResult.ActionOutput {

        String feedbackResponseId;

        String giverIdentifier;

        String recipientIdentifier;

        FeedbackResponseDetails responseDetails;

        public FeedbackResponseResponse(FeedbackResponseAttributes feedbackResponseAttributes) {
            this.feedbackResponseId = feedbackResponseAttributes.getId();
            this.giverIdentifier = feedbackResponseAttributes.giver;
            this.recipientIdentifier = feedbackResponseAttributes.recipient;
            this.responseDetails = feedbackResponseAttributes.getResponseDetails();
        }
    }
}
