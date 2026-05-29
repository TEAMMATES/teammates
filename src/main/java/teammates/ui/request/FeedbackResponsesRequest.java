package teammates.ui.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.SanitizationHelper;

/**
 * The basic request of submitting a list of feedback responses.
 */
public class FeedbackResponsesRequest extends BasicRequest {

    private List<FeedbackResponseRequest> responses = new ArrayList<>();

    public List<FeedbackResponseRequest> getResponses() {
        return responses;
    }

    public void setResponses(List<FeedbackResponseRequest> responses) {
        this.responses = responses;
    }

    public List<String> getRecipients() {
        return responses.stream().map(FeedbackResponseRequest::getRecipient).toList();
    }

    @Override
    public void validate() {
        // No validation necessary; each response entities will be validated separately
    }

    /**
     * The basic request of submitting a feedback response.
     */
    public static class FeedbackResponseRequest extends BasicRequest {

        private String recipient;
        private FeedbackResponseDetails responseDetails;
        private String giverComment;

        public FeedbackResponseRequest(String recipient, FeedbackResponseDetails responseDetails) {
            this(recipient, responseDetails, null);
        }

        @JsonCreator
        public FeedbackResponseRequest(String recipient, FeedbackResponseDetails responseDetails, String giverComment) {
            this.recipient = recipient;
            this.responseDetails = responseDetails;
            this.giverComment = giverComment;
        }

        @Override
        public void validate() throws InvalidHttpRequestBodyException {
            assertTrue(recipient != null && !recipient.isEmpty(), "Recipient cannot be empty");
            assertTrue(responseDetails != null, "Response details cannot be null");
        }

        public String getRecipient() {
            return recipient;
        }

        public FeedbackResponseDetails getResponseDetails() {
            return responseDetails;
        }

        public String getGiverComment() {
            return giverComment == null ? null : SanitizationHelper.sanitizeForRichText(giverComment);
        }

    }

}
