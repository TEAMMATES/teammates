package teammates.ui.newcontroller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.JsonUtils;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Data transfer objects for {@link FeedbackResponseAttributes} between controller and HTTP.
 */
public class FeedbackResponseInfo {

    /**
     * The response of a list of feedback response.
     */
    public static class FeedbackResponsesResponse extends ApiOutput {

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
    public static class FeedbackResponseResponse extends ApiOutput {

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

    /**
     * The basic HTTP body request of a feedback response.
     */
    private static class FeedbackResponseBasicRequest extends Action.RequestBody {

        private String recipientIdentifier;

        private FeedbackQuestionType questionType;

        private Map<String, Object> responseDetails;

        public String getRecipientIdentifier() {
            return recipientIdentifier;
        }

        public FeedbackQuestionType getQuestionType() {
            return questionType;
        }

        public FeedbackResponseDetails getResponseDetails() {
            FeedbackResponseDetails details =
                    JsonUtils.fromJson(JsonUtils.toJson(responseDetails), questionType.getResponseDetailsClass());
            details.setQuestionType(questionType);
            return details;
        }

        @Override
        public void validate() {
            assertTrue(recipientIdentifier != null, "recipientIdentifier cannot be null");
            assertTrue(questionType != null, "questionType cannot be null");
            assertTrue(responseDetails != null, "responseDetails cannot be null");
        }
    }

    /**
     * The create request of a feedback response.
     */
    public static class FeedbackResponseCreateRequest extends FeedbackResponseBasicRequest {

    }

    /**
     * The save request of a feedback response.
     */
    public static class FeedbackResponseSaveRequest extends FeedbackResponseBasicRequest {

    }
}
