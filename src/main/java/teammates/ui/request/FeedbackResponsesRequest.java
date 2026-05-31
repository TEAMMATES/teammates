package teammates.ui.request;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.SanitizationHelper;

/**
 * The basic request of submitting a list of feedback responses.
 */
public class FeedbackResponsesRequest extends BasicRequest {

    private Map<UUID, List<FeedbackResponseRequest>> questionResponses;

    public Map<UUID, List<FeedbackResponseRequest>> getQuestionResponses() {
        return questionResponses;
    }

    public void setQuestionResponses(Map<UUID, List<FeedbackResponseRequest>> questionResponses) {
        this.questionResponses = questionResponses;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(questionResponses != null, "Question responses cannot be null");
        assertTrue(!questionResponses.isEmpty(), "Question responses cannot be empty");

        for (Map.Entry<UUID, List<FeedbackResponseRequest>> entry : questionResponses.entrySet()) {
            UUID questionId = entry.getKey();
            List<FeedbackResponseRequest> responses = entry.getValue();

            assertTrue(questionId != null, "Question ID cannot be null");
            assertTrue(responses != null, "Responses cannot be null");

            Set<UUID> submittedResponseIds = new HashSet<>();
            Set<String> submittedRecipients = new HashSet<>();

            for (FeedbackResponseRequest response : responses) {
                assertTrue(response != null, "Response cannot be null");
                response.validate();
                UUID responseId = response.getResponseId();
                if (responseId != null) {
                    assertTrue(submittedResponseIds.add(responseId), "Response IDs cannot be duplicated");
                }
                assertTrue(submittedRecipients.add(response.getRecipient()), "Recipients cannot be duplicated");
            }
        }
    }

    /**
     * The basic request of submitting a feedback response.
     */
    public static class FeedbackResponseRequest extends BasicRequest {

        @Nullable
        private UUID responseId;
        private String recipient;
        private FeedbackResponseDetails responseDetails;
        private String giverComment;

        public FeedbackResponseRequest(String recipient, FeedbackResponseDetails responseDetails) {
            this(null, recipient, responseDetails, null);
        }

        @JsonCreator
        public FeedbackResponseRequest(
                UUID responseId, String recipient, FeedbackResponseDetails responseDetails, String giverComment) {
            this.responseId = responseId;
            this.recipient = recipient;
            this.responseDetails = responseDetails;
            this.giverComment = giverComment == null
                    ? null
                    : SanitizationHelper.sanitizeForRichText(giverComment);
        }

        @Override
        public void validate() throws InvalidHttpRequestBodyException {
            assertTrue(recipient != null && !recipient.isEmpty(), "Recipient cannot be empty");
            assertTrue(responseDetails != null, "Response details cannot be null");
        }

        public UUID getResponseId() {
            return responseId;
        }

        public String getRecipient() {
            return recipient;
        }

        public FeedbackResponseDetails getResponseDetails() {
            return responseDetails;
        }

        public String getGiverComment() {
            return giverComment;
        }

    }

}
