package teammates.ui.request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;

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
        return responses.stream().map(FeedbackResponseRequest::getRecipient).collect(Collectors.toList());
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

        public FeedbackResponseRequest(String recipient, FeedbackResponseDetails responseDetails) {
            this.recipient = recipient;
            this.responseDetails = responseDetails;
        }

        @Override
        public void validate() {
            assertTrue(recipient != null && !recipient.isEmpty(), "Recipient cannot be empty");
            assertTrue(responseDetails != null, "Response details cannot be null");
        }

        public String getRecipient() {
            return recipient;
        }

        public FeedbackResponseDetails getResponseDetails() {
            return responseDetails;
        }

    }

}
